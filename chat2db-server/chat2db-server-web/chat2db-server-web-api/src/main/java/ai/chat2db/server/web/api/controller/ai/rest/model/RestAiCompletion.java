package ai.chat2db.server.web.api.controller.ai.rest.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author moji
 * @version RestAiCompletion.java, v 0.1 2023年05月27日 14:00 moji Exp $
 * @date 2023/05/27
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RestAiCompletion implements Serializable {

    /**
     * 提示语
     */
    private String prompt;

}
