package ai.chat2db.server.domain.api.param;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Delete table structure
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DropParam {
    /**
     * Corresponding source id stored in the database
     */
    @NotNull
    private Long dataSourceId;

    /**
     * Corresponding connection database name
     */
    @NotNull
    private String databaseName;

    /**
     * Table Name
     */
    private String tableName;

    /**
     * schema
     */
    private String tableSchema;
}
