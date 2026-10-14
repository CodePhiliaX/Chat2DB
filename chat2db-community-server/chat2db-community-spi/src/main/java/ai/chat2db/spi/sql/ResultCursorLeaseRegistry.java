package ai.chat2db.spi.sql;

import ai.chat2db.community.domain.api.model.result.snapshot.ResultCursorHold;
import ai.chat2db.spi.model.datasource.ConnectInfo;
import lombok.extern.slf4j.Slf4j;

import java.sql.Statement;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Keeps the cursors behind leased large values alive.
 * <p>
 * A driver that hands over a {@code Clob} or a {@code Blob} has not transferred the content yet, so the value can be
 * read from its cursor later instead of being copied during the query. That only works while the cursor and its
 * connection live: the execution announces them with {@link #begin(ConnectInfo, Statement)}, the large value layer
 * takes a {@link #holdForCurrentExecution()} for every snapshot that leases a value, and the execution asks
 * {@link #isPinned()} before it closes anything. The registry closes the cursor and gives the connection back once the
 * last hold is gone, when a lease has been idle for too long, or on shutdown.
 */
@Slf4j
public final class ResultCursorLeaseRegistry {

    /**
     * How many leases one process may keep open. Every lease holds a statement and a connection, so the number stays
     * small on purpose: a value that cannot get a lease is captured into the snapshot the usual way instead.
     */
    static final int MAX_LEASES = 4;

    /**
     * A lease nobody read from for this long is closed, so a forgotten tab can never keep a connection forever.
     */
    static final long IDLE_TIMEOUT_MS = 5 * 60 * 1000L;

    private static final long SWEEP_INTERVAL_MS = 60 * 1000L;

    /**
     * Cursor the current thread is streaming, announced by the executor for the duration of one execution.
     */
    private static final ThreadLocal<Scope> CURRENT_SCOPE = new ThreadLocal<>();

    private static final Map<String, Lease> LEASES = new ConcurrentHashMap<>();

    static {
        Thread sweeper = new Thread(ResultCursorLeaseRegistry::sweepForever, "chat2db-cursor-lease-sweeper");
        sweeper.setDaemon(true);
        sweeper.start();
    }

    private ResultCursorLeaseRegistry() {
    }

    /**
     * Announces the cursor of the execution running on this thread.
     */
    public static void begin(ConnectInfo connectInfo, Statement statement) {
        CURRENT_SCOPE.set(new Scope(connectInfo, statement));
    }

    /**
     * Ends the announcement. A cursor that was leased stays registered until its holds are gone.
     */
    public static void end() {
        CURRENT_SCOPE.remove();
    }

    /**
     * @return {@code true} when the execution on this thread leased a value, so it must not close its cursor
     */
    public static boolean isPinned() {
        Scope scope = CURRENT_SCOPE.get();
        return scope != null && scope.lease != null;
    }

    /**
     * Takes a hold on the cursor of the execution on this thread, so a snapshot can read the leased values later.
     *
     * @return the hold the snapshot closes when it is released, or {@code null} when this execution cannot lease (no
     *         cursor announced, no connection, or the process already holds {@link #MAX_LEASES} of them)
     */
    public static ResultCursorHold holdForCurrentExecution() {
        Scope scope = CURRENT_SCOPE.get();
        if (scope == null || scope.connectInfo == null || scope.statement == null) {
            return null;
        }
        Lease lease = scope.lease;
        if (lease == null) {
            if (LEASES.size() >= MAX_LEASES) {
                log.warn("No cursor lease available, {} are already open", LEASES.size());
                return null;
            }
            lease = new Lease(UUID.randomUUID().toString(), scope.connectInfo, scope.statement);
            scope.lease = lease;
            LEASES.put(lease.getId(), lease);
        }
        lease.touch();
        lease.hold();
        return new LeaseHold(lease);
    }

    /**
     * @return {@code true} while a leased value still reads from this connection, so its teardown must not close it
     */
    static boolean isHeld(ConnectInfo connectInfo) {
        for (Lease lease : LEASES.values()) {
            if (lease.getConnectInfo() == connectInfo) {
                return true;
            }
        }
        return false;
    }

    /**
     * Closes every lease, used when the process shuts down.
     */
    public static void releaseAll() {
        for (Lease lease : LEASES.values()) {
            closeLease(lease);
        }
    }

    /**
     * Closes the leases that nobody read from for {@link #IDLE_TIMEOUT_MS}.
     *
     * @return the number of leases that were closed
     */
    static int evictIdle(long now) {
        int closed = 0;
        for (Lease lease : LEASES.values()) {
            if (now - lease.getLastActivityAt() > IDLE_TIMEOUT_MS) {
                closeLease(lease);
                closed++;
            }
        }
        return closed;
    }

    static int openLeases() {
        return LEASES.size();
    }

    private static void closeLease(Lease lease) {
        if (LEASES.remove(lease.getId()) == null) {
            return;
        }
        log.debug("Closing the cursor lease of {}", lease.getConnectInfo().getKey());
        try {
            lease.getStatement().close();
        } catch (Exception e) {
            log.warn("Failed to close the leased statement: {}", e.getMessage());
        }
        ConnectionPool.close(lease.getConnectInfo());
    }

    private static void sweepForever() {
        while (true) {
            try {
                Thread.sleep(SWEEP_INTERVAL_MS);
                evictIdle(System.currentTimeMillis());
            } catch (InterruptedException e) {
                return;
            } catch (Exception e) {
                log.warn("Failed to sweep cursor leases", e);
            }
        }
    }

    /**
     * Cursor the current thread announced, and the lease it produced.
     */
    private static final class Scope {

        private final ConnectInfo connectInfo;
        private final Statement statement;
        private Lease lease;

        private Scope(ConnectInfo connectInfo, Statement statement) {
            this.connectInfo = connectInfo;
            this.statement = statement;
        }
    }

    private static final class Lease {

        private final String id;
        private final ConnectInfo connectInfo;
        private final Statement statement;
        private final AtomicInteger holds = new AtomicInteger();
        private final AtomicLong lastActivityAt = new AtomicLong(System.currentTimeMillis());

        private Lease(String id, ConnectInfo connectInfo, Statement statement) {
            this.id = id;
            this.connectInfo = connectInfo;
            this.statement = statement;
        }

        private String getId() {
            return id;
        }

        private ConnectInfo getConnectInfo() {
            return connectInfo;
        }

        private Statement getStatement() {
            return statement;
        }

        private long getLastActivityAt() {
            return lastActivityAt.get();
        }

        private void touch() {
            lastActivityAt.set(System.currentTimeMillis());
        }

        private void hold() {
            holds.incrementAndGet();
        }

        private boolean drop() {
            touch();
            return holds.decrementAndGet() <= 0;
        }
    }

    /**
     * One snapshot's hold on a lease: the cursor lives until every snapshot that reads from it is released.
     */
    private static final class LeaseHold implements ResultCursorHold {

        private final Lease lease;
        private final AtomicBoolean closed = new AtomicBoolean();

        private LeaseHold(Lease lease) {
            this.lease = lease;
        }

        @Override
        public void close() {
            if (!closed.compareAndSet(false, true)) {
                return;
            }
            if (lease.drop()) {
                closeLease(lease);
            }
        }
    }
}
