package ai.chat2db.server.domain.api.param;

import ai.chat2db.server.tools.base.wrapper.param.PageQueryParam;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Pagination query table information
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TablePageQueryParam extends PageQueryParam {
    private static final long serialVersionUID = 8054519332890887747L;
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
    private String schemaName;



    /**
     * if true, refresh the cache
     */
    private boolean refresh;


    private String searchKey;
}
