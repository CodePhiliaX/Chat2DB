package ai.chat2db.server.domain.api.param;


import ai.chat2db.server.tools.base.wrapper.param.PageQueryParam;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Pagination query sequence information
 *
 * @author Sylphy
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SequencePageQueryParam extends PageQueryParam {
    private static final long serialVersionUID = 1364512325486354343L;

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
     * schema
     */
    private String schemaName;
}
