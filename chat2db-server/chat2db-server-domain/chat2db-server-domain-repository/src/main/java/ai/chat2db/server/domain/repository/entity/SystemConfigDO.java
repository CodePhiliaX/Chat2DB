
package ai.chat2db.server.domain.repository.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jipengfei
 * @version : SystemConfigDO.java
 */
@Getter
@Setter
@TableName("SYSTEM_CONFIG")
public class SystemConfigDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * primary key
     */
    @TableId(value = "ID", type = IdType.AUTO)
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