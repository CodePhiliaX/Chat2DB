package ai.chat2db.server.domain.api.param;

import ai.chat2db.spi.model.OrderBy;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;


@Data
public class OrderByParam {

    /**
     * console id
     */
    @NotNull
    private Long consoleId;

    /**
     * Data source id
     */
    @NotNull
    private Long dataSourceId;

    /**
     * databaseName
     */
    private String databaseName;


    /**
     * schema name
     */
    private String schemaName;


    /**
     * origin sql
     */
    private String originSql;


    /**
     * sort field
     */
    private List<OrderBy> orderByList;
}
