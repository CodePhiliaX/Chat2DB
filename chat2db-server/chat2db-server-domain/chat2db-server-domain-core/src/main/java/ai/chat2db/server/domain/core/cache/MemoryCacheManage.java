package ai.chat2db.server.domain.core.cache;

import java.io.Serializable;
import java.time.Duration;
import java.util.function.Supplier;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.ehcache.Cache;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.springframework.cache.support.NullValue;

/**
 * It will only be stored in memory
 *
 * @author Jiaju Zhuang
 */
public class MemoryCacheManage {

    private static final byte[] NULL_BYTES = SerializationUtils.serialize((NullValue)NullValue.INSTANCE);
    private static final String CACHE = "memory_cache";
    private static final String SYNCHRONIZED_PREFIX = "MemoryCache:";

    private static Cache<String, byte[]> cache;

    static {
        cache = CacheManagerBuilder.newCacheManagerBuilder()
            .build(true)
            .createCache(CACHE,
                CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, byte[].class,
                        ResourcePoolsBuilder.newResourcePoolsBuilder()
                            .offheap(10, MemoryUnit.MB))
                    .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(10))));
    }

    /**
     * Retrieve a value from the cache, and if not, query it
     * The timeout is fixed at 10 minutes
     *
     * @param key
     * @param queryData
     * @param <T>
     * @return
     */
    public static <T extends Serializable> T computeIfAbsent(String key, Supplier<T> queryData) {
        if (key == null) {
            return null;
        }
        T data = get(key);
        if (data != null) {
            return data;
        }
        String lockKey = SYNCHRONIZED_PREFIX + key;
        synchronized (lockKey.intern()) {
            data = get(key);
            if (data != null) {
                return data;
            }

            T value = queryData.get();
            put(key, value);
            return value;
        }
    }

    /**
     * Get a data from cache
     *
     * @param key
     * @param <T>
     * @return
     */
    public static <T> T get(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        byte[] bytes = cache.get(key);
        if (bytes == null) {
            return null;
        }
        T data = SerializationUtils.deserialize(bytes);
        if (NullValue.INSTANCE.equals(data)) {
            return null;
        }
        return data;
    }

    /**
     * Put a data from cache
     * The timeout is fixed at 10 minutes
     *
     * @param key
     * @param value
     */
    public static void put(String key, Serializable value) {
        if (key == null) {
            return;
        }
        if (value == null) {
            cache.put(key, NULL_BYTES);
        } else {
            cache.put(key, SerializationUtils.serialize(value));
        }
    }

}
