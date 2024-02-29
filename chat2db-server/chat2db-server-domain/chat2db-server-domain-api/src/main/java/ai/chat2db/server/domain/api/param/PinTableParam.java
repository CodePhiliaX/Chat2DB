package ai.chat2db.server.domain.api.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PinTableParam {

    @NotNull
    private Long dataSourceId;

    /**
     * databaseName
     */
    private String databaseName;

    /**
     * The space where the table is located
     */
    private String schemaName;

    /**
     * tableName
     */
    private String tableName;

    /**
     * pin userId
     */
    private Long userId;
}
