package ai.chat2db.server.domain.api.param;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TableVectorParam {

    /**
     * api key
     */
    @NotNull
    private String apiKey;

    /**
     * Data source connection ID
     */
    private Long dataSourceId;

    /**
     * database name
     */
    private String database;

    /**
     * schema name
     */
    private String schema;

    /**
     * Vector saved state
     */
    private String status;
}
