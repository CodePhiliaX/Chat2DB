
package ai.chat2db.server.domain.api.param;

import java.sql.Connection;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jipengfei
 * @version : SchemaQueryParam.java
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SchemaQueryParam {

    @NotNull
    private Long dataSourceId;

    private String dataBaseName;



    /**
     * if true, refresh the cache
     */
    private boolean refresh;

    /**
     * Can be null, if null, use the default connection
     */
    private Connection connection;
}