package ai.chat2db.server.domain.api.param;


import ai.chat2db.server.tools.base.wrapper.param.QueryParam;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;

/**
 * Sequence query param
 *
 * @author Sylphy
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SequenceQueryParam extends QueryParam {
    @Serial
    private static final long serialVersionUID = -6918238998725081254L;
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
     * Sequence Name
     */
    private String sequenceName;

    /**
     * Space name
     */
    private String schemaName;

    private boolean refresh;
}
