package ai.chat2db.server.web.api.controller.ai.fastchat.embeddings;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.unfbx.chatgpt.exception.BaseException;
import com.unfbx.chatgpt.exception.CommonError;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Objects;

/**
 * 描述：
 *
 * @author https:www.unfbx.com
 *  2023-02-15
 */
@Getter
@Slf4j
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class FastChatEmbedding implements Serializable {
    @NonNull
    @Builder.Default
    private String model = Model.TEXT_EMBEDDING_ADA_002.getName();
    /**
     * 必选项：长度不能超过：8192
     */
    @NonNull
    private String input;

    private String user;

    public void setModel(Model model) {
        if (Objects.isNull(model)) {
            model = Model.TEXT_EMBEDDING_ADA_002;
        }
        this.model = model.getName();
    }

    public void setModel(String model) {
        if (Objects.isNull(model)) {
            model = Model.TEXT_EMBEDDING_ADA_002.getName();
        }
        this.model = model;
    }

    public void setInput(String input) {
        if (input == null || "".equals(input)) {
            log.error("input不能为空");
            throw new BaseException(CommonError.PARAM_ERROR);
        }
        if (input.length() > 8192) {
            log.error("input超长");
            throw new BaseException(CommonError.PARAM_ERROR);
        }
        this.input = input;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Getter
    @AllArgsConstructor
    public enum Model {
        TEXT_EMBEDDING_ADA_002("text-embedding-ada-002"),
        ;
        private String name;
    }
}
