package ai.chat2db.server.web.api.controller.ai.config;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.DateUnit;

/**
 * description：
 *
 * @author https:www.unfbx.com
 * @date 2023-03-10
 */
public class LocalCache {
    /**
     * Cache duration
     */
    public static final long TIMEOUT = 5 * DateUnit.MINUTE.getMillis();
    /**
     * Cleanup interval
     */
    private static final long CLEAN_TIMEOUT = 5 * DateUnit.MINUTE.getMillis();
    /**
     * Cache object
     */
    public static final TimedCache<String, Object> CACHE = CacheUtil.newTimedCache(TIMEOUT);

    static {
        //Start a scheduled task
        CACHE.schedulePrune(CLEAN_TIMEOUT);
    }
}
