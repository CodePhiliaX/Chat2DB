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
 * Custom dashboard
 * </p>
 *
 * @author ali-dbhub
 * @since 2023-06-09
 */
@Getter
@Setter
@TableName("DASHBOARD_CHART_RELATION")
public class DashboardChartRelationDO implements Serializable {

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
     * report id
     */
    private Long dashboardId;

    /**
     * chart id
     */
    private Long chartId;
}
