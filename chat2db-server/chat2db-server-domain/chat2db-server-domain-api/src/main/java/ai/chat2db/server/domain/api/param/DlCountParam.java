package ai.chat2db.server.domain.api.param;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * total number
 *
 * @author Shi Yi
 */
@Data
public class DlCountParam {

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
}
