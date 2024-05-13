package ai.chat2db.server.domain.api.param;

import java.io.Serial;

import jakarta.validation.constraints.NotNull;

import ai.chat2db.server.tools.base.wrapper.param.QueryParam;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Query table information
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TableQueryParam extends QueryParam {
    @Serial
    private static final long serialVersionUID = -8918610899872508804L;
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
     * Space name
     */
    private String schemaName;

    private boolean refresh;
}
