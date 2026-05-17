package ai.chat2db.server.web.api.controller.ai.prompt;

/**
 * 提示词构建器接口
 */
public interface PromptBuilder {

    /**
     * 设置上下文
     *
     * @param context 上下文
     * @return 构建器
     */
    PromptBuilder context(PromptContext context);

    /**
     * 设置消息
     *
     * @param message 消息内容
     * @return 构建器
     */
    PromptBuilder message(String message);

    /**
     * 设置扩展信息
     *
     * @param ext 扩展信息
     * @return 构建器
     */
    PromptBuilder ext(String ext);

    /**
     * 设置 Schema DDL
     *
     * @param schemaDdl Schema DDL
     * @return 构建器
     */
    PromptBuilder schema(String schemaDdl);

    /**
     * 设置数据源类型
     *
     * @param dataSourceType 数据源类型
     * @return 构建器
     */
    PromptBuilder dataSourceType(String dataSourceType);

    /**
     * 设置目标 SQL 类型
     *
     * @param targetSqlType 目标 SQL 类型
     * @return 构建器
     */
    PromptBuilder targetSqlType(String targetSqlType);

    /**
     * 设置源文件字段列表（用于字段映射推荐）
     *
     * @param sourceFields 源文件字段列表（JSON 格式）
     * @return 构建器
     */
    PromptBuilder sourceFields(String sourceFields);

    /**
     * 构建提示词
     *
     * @return 提示词
     */
    String build();

    /**
     * 验证提示词
     *
     * @return 是否有效
     */
    boolean validate();
}
