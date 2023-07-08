package ai.chat2db.server.domain.core.cache;

import java.io.File;

import com.alibaba.fastjson2.JSON;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.springframework.util.StringUtils;

public class CacheManage {
    private static final String PATH = System.getProperty("user.home") + File.separator + ".chat2db"
        + File.separator
        + "cache" + File.separator + "chat2db-ehcache-data";

    private static final String CACHE = "meta_cache";
    private static CacheManager cacheManager;

    static {
         cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
            .with(CacheManagerBuilder.persistence(PATH)) // 确保这个路径是存在且有写权限的
            .withCache(CACHE, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class,
                ResourcePoolsBuilder.newResourcePoolsBuilder()
                    .heap(1, EntryUnit.ENTRIES)
                    .disk(20, MemoryUnit.GB, true))) // 磁盘持久化设置为true
            .build(true);
    }

    public static <T> T get(String key,Class<T> clazz) {
        Cache<String, String> myCache = cacheManager.getCache(CACHE, String.class, String.class);
        String value = myCache.get(key);
        if(!StringUtils.isEmpty(value)){
            return JSON.parseObject(value,clazz);
        }
        return null;
    }



    public static void put(String s, Object value) {
        Cache<String, String> myCache = cacheManager.getCache(CACHE, String.class, String.class);
        myCache.put(s, JSON.toJSONString(value));
    }

    public static void  close() {
        cacheManager.close();
    }


}
