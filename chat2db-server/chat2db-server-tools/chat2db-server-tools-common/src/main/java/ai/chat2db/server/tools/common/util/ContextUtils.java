package ai.chat2db.server.tools.common.util;

import ai.chat2db.server.tools.common.exception.NeedLoggedInBusinessException;
import ai.chat2db.server.tools.common.model.Context;
import ai.chat2db.server.tools.common.model.LoginUser;

import lombok.extern.slf4j.Slf4j;

/**
 * Context tool class
 *
 * @author Jiaju Zhuang
 */
@Slf4j
public class ContextUtils {

    /**
     * Store context
     */
    private static final ThreadLocal<Context> CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * Get user id
     *
     * @return
     */
    public static Long getUserId() {
        return getLoginUser().getId();
    }

    /**
     * Get user information
     *
     * @return may return empty
     */
    public static LoginUser queryLoginUser() {
        // Go to get login information
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
     * Get user information
     *
     * @return If it cannot be obtained, a re-login exception will be thrown.
     */
    public static LoginUser getLoginUser() {
        // Go to get login information
        Context context = queryContext();
        if (context != null && context.getLoginUser() != null) {
            return context.getLoginUser();
        }
        // Determine that the user must log in
        throw new NeedLoggedInBusinessException();
    }

    /**
     * query context
     *
     * @return The interceptor of SaTokenWebMvcConfigurer, when called elsewhere, at least a Context will be returned, and there will be at least tokenValue in it.
     */
    public static Context queryContext() {
        return CONTEXT_THREAD_LOCAL.get();
    }

    /**
     * Set context
     *
     * @param context
     * @return
     */
    public static void setContext(Context context) {
        CONTEXT_THREAD_LOCAL.set(context);
    }

    /**
     * remove context
     */
    public static void removeContext() {
        CONTEXT_THREAD_LOCAL.remove();
    }
}
