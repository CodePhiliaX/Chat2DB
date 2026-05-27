package ai.chat2db.server.domain.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * 数据源连接用户排序表
 *
 * @author chat2db
 */
@Getter
@Setter
@TableName("DATA_SOURCE_SORT")
public class DataSourceSortDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    private Date gmtCreate;

    private Date gmtModified;

    private Long userId;

    private Long dataSourceId;

    private Integer sort;
}
