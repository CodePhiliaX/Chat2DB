
package ai.chat2db.server.domain.api.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * @author jipengfei
 * @version : Config.java
 */
@Data
public class Config implements Serializable {

    @Serial
    private static final long serialVersionUID = 8377899386569086415L;
    private Long id;

    /**
     * creation time
     */
    private LocalDateTime gmtCreate;

    /**
     * modified time
     */
    private LocalDateTime gmtModified;

    /**
     * Configuration item code
     */
    private String code;

    /**
     * Configuration item content
     */
    private String content;


    /**
     * Configuration summary
     */
    private String summary;
}