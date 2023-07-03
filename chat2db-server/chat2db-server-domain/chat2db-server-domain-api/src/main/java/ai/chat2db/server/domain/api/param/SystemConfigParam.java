
package ai.chat2db.server.domain.api.param;

import java.io.Serial;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jipengfei
 * @version : SystemConfigParam.java
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigParam implements Serializable {
    @Serial
    private static final long serialVersionUID = 7969235263543844658L;

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