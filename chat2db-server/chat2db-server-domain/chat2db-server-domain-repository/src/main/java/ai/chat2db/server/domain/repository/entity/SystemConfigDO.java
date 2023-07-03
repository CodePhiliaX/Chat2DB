
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
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
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