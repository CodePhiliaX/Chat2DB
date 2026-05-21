package ai.chat2db.server.web.api.controller.ai.prompt;

import ai.chat2db.server.web.api.controller.ai.enums.PromptType;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

/**
 * 提示词模板注册表
 * 从配置文件 prompt-templates.yml 加载模板
 */
@Slf4j
@Component
public class PromptTemplateRegistry {

    private static final String CONFIG_FILE = "prompt-templates.yml";

    private final Map<PromptType, PromptTemplate> templates = new EnumMap<>(PromptType.class);

    @PostConstruct
    public void init() {
        loadFromYamlFile();
        ensureAllTypesHaveTemplate();
    }

    /**
     * 从 YAML 文件加载
     */
    private void loadFromYamlFile() {
        log.info("Loading prompt templates from: {}", CONFIG_FILE);
        ClassPathResource resource = new ClassPathResource(CONFIG_FILE);
        if (!resource.exists()) {
            log.warn("Prompt templates config file not found: {}, using defaults", CONFIG_FILE);
            return;
        }

        Yaml yaml = new Yaml();
        try (InputStream inputStream = resource.getInputStream()) {
            Map<String, Object> data = yaml.load(inputStream);
            if (data == null || !data.containsKey("prompts")) {
                log.warn("No prompts configuration found in YAML file");
                return;
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> prompts = (Map<String, Object>) data.get("prompts");
            
            for (Map.Entry<String, Object> entry : prompts.entrySet()) {
                try {
                    PromptType type = PromptType.valueOf(entry.getKey().toUpperCase());
                    @SuppressWarnings("unchecked")
                    Map<String, Object> templateData = (Map<String, Object>) entry.getValue();
                    
                    PromptTemplate template = PromptTemplate.builder()
                            .name((String) templateData.get("name"))
                            .promptType(type)
                            .description((String) templateData.get("description"))
                            .template((String) templateData.get("template"))
                            .build();
                    
                    templates.put(type, template);
                    log.debug("Loaded template: {}", type.getCode());
                } catch (IllegalArgumentException e) {
                    log.warn("Unknown prompt type: {}", entry.getKey());
                }
            }
            
            log.info("Loaded {} prompt templates", templates.size());
        } catch (IOException e) {
            log.error("Failed to load prompt templates from YAML file", e);
        }
    }

    /**
     * 确保所有类型都有模板
     */
    private void ensureAllTypesHaveTemplate() {
        PromptTemplate defaultTemplate = buildDefaultTemplate();
        
        for (PromptType type : PromptType.values()) {
            if (!templates.containsKey(type)) {
                log.warn("Template not found for type: {}, using default", type.getCode());
                templates.put(type, defaultTemplate);
            }
        }
    }

    /**
     * 根据类型获取模板
     *
     * @param type 提示词类型
     * @return 模板
     */
    public PromptTemplate getTemplate(PromptType type) {
        return templates.getOrDefault(type, getDefaultTemplate());
    }


    private PromptTemplate getDefaultTemplate() {
        return templates.getOrDefault(PromptType.NL_2_SQL, buildDefaultTemplate());
    }

    private PromptTemplate buildDefaultTemplate() {
        return PromptTemplate.builder()
                .name("default")
                .promptType(PromptType.NL_2_SQL)
                .description("将自然语言转换成SQL查询")
                .template("### 请根据以下 table properties 和 SQL input{description}. {ext}\n" +
                        "#\n" +
                        "### {db_type} SQL tables, with their properties:\n" +
                        "#\n" +
                        "# {schema}\n" +
                        "#\n" +
                        "#\n" +
                        "### SQL input: {message}")
                .build();
    }
}