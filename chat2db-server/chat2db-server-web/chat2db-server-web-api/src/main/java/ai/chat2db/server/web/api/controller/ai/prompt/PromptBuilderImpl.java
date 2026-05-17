package ai.chat2db.server.web.api.controller.ai.prompt;

import ai.chat2db.server.web.api.controller.ai.enums.PromptType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 提示词构建器实现
 */
@Component
public class PromptBuilderImpl implements PromptBuilder {

    private final PromptTemplateRegistry templateRegistry;
    private final PromptValidator validator;

    private PromptContext context;

    @Autowired
    public PromptBuilderImpl(PromptTemplateRegistry templateRegistry, PromptValidator validator) {
        this.templateRegistry = templateRegistry;
        this.validator = validator;
    }

    @Override
    public PromptBuilder context(PromptContext context) {
        this.context = context;
        return this;
    }

    @Override
    public PromptBuilder message(String message) {
        if (this.context == null) {
            this.context = new PromptContext();
        }
        this.context.setMessage(message);
        return this;
    }

    @Override
    public PromptBuilder ext(String ext) {
        if (this.context == null) {
            this.context = new PromptContext();
        }
        this.context.setExt(ext);
        return this;
    }

    @Override
    public PromptBuilder schema(String schemaDdl) {
        if (this.context == null) {
            this.context = new PromptContext();
        }
        this.context.setSchemaDdl(schemaDdl);
        return this;
    }

    @Override
    public PromptBuilder dataSourceType(String dataSourceType) {
        if (this.context == null) {
            this.context = new PromptContext();
        }
        this.context.setDataSourceType(dataSourceType);
        return this;
    }

    @Override
    public PromptBuilder targetSqlType(String targetSqlType) {
        if (this.context == null) {
            this.context = new PromptContext();
        }
        this.context.setTargetSqlType(targetSqlType);
        return this;
    }

    @Override
    public PromptBuilder sourceFields(String sourceFields) {
        if (this.context == null) {
            this.context = new PromptContext();
        }
        this.context.setSourceFields(sourceFields);
        return this;
    }

    @Override
    public String build() {
        validateContext();

        PromptType type = context.getPromptType();
        if (type == null) {
            type = PromptType.NL_2_SQL;
        }

        PromptTemplate template = templateRegistry.getTemplate(type);
        String builtPrompt = fillTemplate(template, context);

        return validator.cleanPrompt(builtPrompt);
    }

    @Override
    public boolean validate() {
        String builtPrompt = build();
        return validator.isValidLength(builtPrompt);
    }

    private void validateContext() {
        if (context == null) {
            throw new IllegalStateException("PromptContext is null");
        }
        if (StringUtils.isBlank(context.getMessage())
                && !Objects.equals(context.getPromptType(), PromptType.NL_2_COMMENT)
                && !Objects.equals(context.getPromptType(), PromptType.NL_2_COMMENT_BATCH)
                && !Objects.equals(context.getPromptType(), PromptType.NL_2_FIELD_MAPPING)
                && !Objects.equals(context.getPromptType(), PromptType.NL_2_DATA_EXPRESSION)
                && !Objects.equals(context.getPromptType(), PromptType.SQL_FIX)) {
            throw new IllegalArgumentException("Message is required");
        }
    }

    private String fillTemplate(PromptTemplate template, PromptContext context) {
        String templateStr = template.getTemplate();
        String description = context.getPromptType() != null
                ? context.getPromptType().getDescription()
                : "将自然语言转换成 SQL 查询";

        String filledTemplate = templateStr
                .replace("{description}", description)
                .replace("{ext}", Objects.toString(context.getExt(), ""))
                .replace("{db_type}", Objects.toString(context.getDataSourceType(), "MYSQL"))
                .replace("{schema}", Objects.toString(context.getSchemaDdl(), ""))
                .replace("{message}", Objects.toString(context.getMessage(), ""))
                .replace("{target_sql_type}", Objects.toString(context.getTargetSqlType(),
                        Objects.toString(context.getDataSourceType(), "MYSQL")));

        // 处理 source_fields 占位符（用于字段映射）
        if (filledTemplate.contains("{source_fields}")) {
            String sourceFieldsText = formatSourceFields(context.getSourceFields());
            filledTemplate = filledTemplate.replace("{source_fields}", sourceFieldsText);
        }

        // 处理 SQL_FIX 的占位符
        if (filledTemplate.contains("{error_message}") || filledTemplate.contains("{original_sql}")) {
            String errorMessage = "";
            String originalSql = "";
            if (StringUtils.isNotBlank(context.getExt())) {
                try {
                    com.alibaba.fastjson2.JSONObject extJson = com.alibaba.fastjson2.JSON.parseObject(context.getExt());
                    errorMessage = extJson.getString("errorMessage");
                    originalSql = extJson.getString("originalSql");
                } catch (Exception e) {
                    // 忽略解析错误
                }
            }
            filledTemplate = filledTemplate.replace("{error_message}", Objects.toString(errorMessage, ""));
            filledTemplate = filledTemplate.replace("{original_sql}", Objects.toString(originalSql, ""));
        }

        return filledTemplate;
    }

    private String formatSourceFields(String sourceFieldsJson) {
        if (StringUtils.isBlank(sourceFieldsJson)) {
            return "";
        }
        try {
            if (sourceFieldsJson.trim().startsWith("[")) {
                com.alibaba.fastjson2.JSONArray fields = com.alibaba.fastjson2.JSON.parseArray(sourceFieldsJson);
                return fields.stream()
                        .map(field -> "- " + field.toString())
                        .collect(java.util.stream.Collectors.joining("\n"));
            }
            return sourceFieldsJson;
        } catch (Exception e) {
            return sourceFieldsJson;
        }
    }
}
