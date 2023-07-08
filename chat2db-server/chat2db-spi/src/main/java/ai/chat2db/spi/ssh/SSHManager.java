
package ai.chat2db.spi.ssh;

import java.util.concurrent.ConcurrentHashMap;

import ai.chat2db.spi.model.SSHInfo;
import cn.hutool.core.net.NetUtil;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author jipengfei
 * @version : SSHSessionManager.java
 */
@Slf4j
public class SSHManager {

    private static final ConcurrentHashMap<SSHInfo, Session> SSH_SESSION_MAP = new ConcurrentHashMap();

    public static Session getSSHSession(SSHInfo sshInfo) {
        Session session = SSH_SESSION_MAP.get(sshInfo);
        if (session != null && session.isConnected()) {
            return session;
        } else {
            return createSession(sshInfo);
        }
    }

    private static Session createSession(SSHInfo ssh) {
        synchronized (ssh) {
            Session session = SSH_SESSION_MAP.get(ssh);
            if (session != null && session.isConnected()) {
                return session;
            }
            try {
                JSch jSch = new JSch();
                if (StringUtils.isNotBlank(ssh.getKeyFile()) && StringUtils.isNotBlank(ssh.getPassphrase())) {
                    jSch.addIdentity(ssh.getKeyFile(), ssh.getPassphrase());
                }
                session = jSch.getSession(ssh.getUserName(), ssh.getHostName(), Integer.parseInt(ssh.getPort()));
                if (StringUtils.isBlank(ssh.getKeyFile()) || StringUtils.isBlank(ssh.getPassphrase())) {
                    session.setPassword(ssh.getPassword());
                }

                session.setConfig("StrictHostKeyChecking", "no");
                session.connect();
                SSH_SESSION_MAP.put(ssh, session);
            } catch (Exception e) {
                throw new RuntimeException("create ssh session error", e);
            }

            if (StringUtils.isNotBlank(ssh.getRHost()) && StringUtils.isNotBlank(ssh.getRPort())) {
                try {
                    int localPort = !StringUtils.isBlank(ssh.getLocalPort()) ? Integer.parseInt(ssh.getLocalPort())
                        : NetUtil.getUsableLocalPort();
                     session.setPortForwardingL(localPort, ssh.getRHost(),
                        Integer.parseInt(ssh.getRPort()));
                } catch (Exception e) {
                    if (session != null && session.isConnected()) {
                        session.disconnect();
                        SSH_SESSION_MAP.remove(ssh);
                    }
                    throw new RuntimeException(ssh.getLocalPort() + " port is used，please change to another port ", e);
                }
            }
            return session;
        }
    }

    public static void close() {
        SSH_SESSION_MAP.forEach((k, v) -> {
            if (v != null && v.isConnected()) {
                try {
                    v.delPortForwardingL(Integer.parseInt(k.getLocalPort()));
                } catch (Exception e) {
                    log.error("delPortForwardingL error", e);
                }
                try {
                    v.disconnect();
                } catch (Exception e) {
                    log.error("disconnect error", e);
                }
            }
        });
    }
}