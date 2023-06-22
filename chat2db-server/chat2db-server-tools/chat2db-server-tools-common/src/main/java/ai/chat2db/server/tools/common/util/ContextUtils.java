package ai.chat2db.server.tools.common.util;

import ai.chat2db.server.tools.common.exception.NeedLoggedInBusinessException;
import ai.chat2db.server.tools.common.model.Context;
import ai.chat2db.server.tools.common.model.LoginUser;

import lombok.extern.slf4j.Slf4j;

/**
 * 上下文工具类
 *
 * @author Jiaju Zhuang
 */
@Slf4j
public class ContextUtils {

    /**
     * 存储context
     */
    private static final ThreadLocal<Context> CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 获取用户id
     *
     * @return
     */
    public static Long getUserId() {
        return getLoginUser().getId();
    }

    /**
     * 获取用户信息
     *
     * @return 可能返回为空
     */
    public static LoginUser queryLoginUser() {
        // 去登录信息获取
        Context context = queryContext();
        if (context == null) {
            return null;
        }
        if (context.getLoginUser() == null) {
            return null;
        }
        return context.getLoginUser();
    }

    /**
     * 获取用户信息
     *
     * @return 拿不到会抛出重新登陆异常
     */
    public static LoginUser getLoginUser() {
        // 去登录信息获取
        Context context = queryContext();
        if (context != null && context.getLoginUser() != null) {
            return context.getLoginUser();
        }
        // 判断用户必须登录
        throw new NeedLoggedInBusinessException();
    }

    /**
     * 查询上下文
     *
     * @return SaTokenWebMvcConfigurer的拦截器，其他地方调用至少 会返回一个Context ，且里面至少有tokenValue
     */
    public static Context queryContext() {
        return CONTEXT_THREAD_LOCAL.get();
    }

    /**
     * 设置上下文 设置上下文
     *
     * @param context
     * @return
     */
    public static void setContext(Context context) {
        CONTEXT_THREAD_LOCAL.set(context);
    }

    /**
     * 移除上下文
     */
    public static void removeContext() {
        CONTEXT_THREAD_LOCAL.remove();
    }
}
