package ai.chat2db.server.web.api.controller.ai.prompt;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 提示词验证器
 */
@Component
public class PromptValidator {

    private static final int MAX_PROMPT_LENGTH = 15400;
    private static final int TOKEN_CONVERT_CHAR_LENGTH = 4;

    /**
     * 验证提示词长度
     *
     * @param prompt 提示词内容
     * @return 是否有效
     */
    public boolean isValidLength(String prompt) {
        if (StringUtils.isEmpty(prompt)) {
            return false;
        }
        return getTokenCount(prompt) <= MAX_PROMPT_LENGTH;
    }

    /**
     * 获取 token 数量
     *
     * @param prompt 提示词内容
     * @return token 数量
     */
    public int getTokenCount(String prompt) {
        if (StringUtils.isEmpty(prompt)) {
            return 0;
        }
        return prompt.length() / TOKEN_CONVERT_CHAR_LENGTH;
    }

    /**
     * 清理提示词（移除特殊字符）
     *
     * @param prompt 原始提示词
     * @return 清理后的提示词
     */
    public String cleanPrompt(String prompt) {
        if (StringUtils.isEmpty(prompt)) {
            return "";
        }
        return prompt.replaceAll("[\r\t]", "");
    }
}
