package ai.chat2db.server.web.api.controller.ai.statemachine;
/**
 * 聊天状态枚举
 * 定义AI聊天交互过程中的各种状态
 */
public enum ChatState {
    /** 空闲状态，初始状态或等待用户输入 */
    IDLE,
    
    /** 自动选择表状态，系统正在自动选择合适的数据库表 */
    AUTO_SELECTING_TABLES,
    
    /** 获取表结构状态，正在获取选中表的schema信息 */
    FETCHING_TABLE_SCHEMA,
    
    /** 执行EXPLAIN状态，正在获取SQL执行计划 */
    EXECUTING_EXPLAIN,
    
    /** 构建提示词状态，正在构造发送给AI的prompt */
    BUILDING_PROMPT,
    
    /** 流式输出状态，正在接收并流式返回AI的响应 */
    STREAMING,
    
    /** 完成状态，聊天交互已成功完成 */
    COMPLETED,
    
    /** 失败状态，聊天交互过程中发生错误 */
    FAILED
}