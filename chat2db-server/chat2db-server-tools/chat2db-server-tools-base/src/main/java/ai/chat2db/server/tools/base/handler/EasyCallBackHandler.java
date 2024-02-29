package ai.chat2db.server.tools.base.handler;

/**
 * Callback handler
 * For example, this method will be executed when meatq calls back.
 *
 * @author Shi Yi
 */
public interface EasyCallBackHandler {
    /**
     * Called before handling the callback
     */
    default void preHandle() {
    }

    /**
     * Called after handling the callback
     * If an exception is thrown, it will not be handled.
     */
    default void postHandle() {
    }

    /**
     * Called after handling the callback
     * Will be called regardless of whether there is an exception or not
     */
    default void afterCompletion() {
    }
}
