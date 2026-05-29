package ai.chat2db.server.web.api.controller.ai.statemachine;

/**
 * 聊天状态机事件枚举
 * 定义了AI对话过程中可能发生的各种事件
 */
public enum ChatEvent {
    /** 表已提供 */
    TABLES_PROVIDED,
    /** 表未提供 */
    TABLES_NOT_PROVIDED,
    /** 表无需提供（如文本生成、标题生成） */
    TABLES_NOT_NEEDED,
    /** 自动选择完成 */
    AUTO_SELECT_DONE,
    /** Schema已获取 */
    SCHEMA_FETCHED,
    /** EXPLAIN执行完成 */
    EXPLAIN_EXECUTED,
    /** EXPLAIN执行失败 */
    EXPLAIN_FAILED,
    /** 不需要EXPLAIN */
    EXPLAIN_NOT_NEEDED,
    /** Prompt已构建 */
    PROMPT_BUILT,
    /** 流式响应完成 */
    STREAM_FINISHED,
    /** 自动选择失败 */
    AUTO_SELECT_FAILED,
    /** 获取Schema失败 */
    FETCH_SCHEMA_FAILED,
    /** Prompt构建失败 */
    PROMPT_BUILD_FAILED,
    /** AI调用失败 */
    AI_CALL_FAILED,
    /** 取消操作 */
    CANCEL
}