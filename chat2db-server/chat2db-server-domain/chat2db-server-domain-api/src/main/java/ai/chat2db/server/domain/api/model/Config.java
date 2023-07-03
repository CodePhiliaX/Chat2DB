
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
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    private LocalDateTime gmtModified;

    /**
     * 配置项code
     */
    private String code;

    /**
     * 配置项内容
     */
    private String content;


    /**
     * 配置摘要
     */
    private String summary;
}