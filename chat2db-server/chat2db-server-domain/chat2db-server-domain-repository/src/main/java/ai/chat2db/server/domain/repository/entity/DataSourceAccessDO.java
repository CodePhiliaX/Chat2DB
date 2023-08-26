package ai.chat2db.server.domain.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 数据源授权
 * </p>
 *
 * @author chat2db
 * @since 2023-08-26
 */
@Getter
@Setter
@TableName("DATA_SOURCE_ACCESS")
public class DataSourceAccessDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 创建人用户id
     */
    private Long createUserId;

    /**
     * 修改人用户id
     */
    private Long modifiedUserId;

    /**
     * 数据源id
     */
    private Long dataSourceId;

    /**
     * 授权类型
     */
    private String accessObjectType;

    /**
     * 授权id,根据类型区分是用户还是团队
     */
    private Long accessObjectId;
}
