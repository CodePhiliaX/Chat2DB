package ai.chat2db.server.domain.api.param;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author moji
 * @version DataSourceExecuteParam.java, v 0.1 October 14, 2022 13:53 moji Exp $
 * @date 2022/10/14
 */
@Data
public class DlExecuteParam {

    /**
     * sql statement
     */
    @NotNull
    private String sql;

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
    @NotNull
    private String databaseName;


    /**
     * schema name
     */
    private String schemaName;

    /**
     * Page coding
     * Only the select statement has
     */
    private Integer pageNo;

    /**
     * Paging Size
     * Only the select statement has
     */
    private Integer pageSize;

    /**
     * Return all data
     * Only the select statement has
     */
    private Boolean pageSizeAll;
}
