package ai.chat2db.server.domain.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 数据库连接环境
 * </p>
 *
 * @author chat2db
 * @since 2023-07-30
 */
@Getter
@Setter
@TableName("ENVIRONMENT")
public class EnvironmentDO implements Serializable {

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
     * 创建人用户id
     */
    private Long createUserId;

    /**
     * 修改人用户id
     */
    private Long modifiedUserId;

    /**
     * 环境名称
     */
    private String name;

    /**
     * 环境缩写
     */
    private String shortName;

    /**
     * 样式类型
     */
    private String style;
}
