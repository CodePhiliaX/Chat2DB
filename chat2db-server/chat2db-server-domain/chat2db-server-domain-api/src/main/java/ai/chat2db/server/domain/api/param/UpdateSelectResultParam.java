package ai.chat2db.server.domain.api.param;

import ai.chat2db.spi.model.Header;
import ai.chat2db.spi.model.ResultOperation;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UpdateSelectResultParam {
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
     * List of display headers
     */
    @NotEmpty
    private List<Header> headerList;


    /**
     * List of modified data
     */
    @NotEmpty
    private List<ResultOperation> operations;


    /**
     * Table Name
     */
    @NotEmpty
    private String tableName;
}
