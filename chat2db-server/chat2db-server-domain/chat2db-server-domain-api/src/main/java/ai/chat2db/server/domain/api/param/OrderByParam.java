package ai.chat2db.server.domain.api.param;

import ai.chat2db.spi.model.OrderBy;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;


@Data
public class OrderByParam {

    /**
     * 控制台id
     */
    @NotNull
    private Long consoleId;

    /**
     * 数据源id
     */
    @NotNull
    private Long dataSourceId;

    /**
     * DB名称
     */
    private String databaseName;


    /**
     * schema名称
     */
    private String schemaName;


    /**
     * origin sql
     */
    private String originSql;


    /**
     * 排序字段
     */
    private List<OrderBy> orderByList;
}
