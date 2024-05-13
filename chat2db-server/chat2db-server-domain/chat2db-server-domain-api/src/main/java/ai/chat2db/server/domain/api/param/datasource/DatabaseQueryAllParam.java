package ai.chat2db.server.domain.api.param.datasource;

import java.sql.Connection;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Display database information
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseQueryAllParam {
    /**
     * Corresponding source id stored in the database
     */
    @NotNull
    private Long dataSourceId;

    /**
     * if true, refresh the cache
     */
    private boolean refresh;

    /**
     * Can be null, if null, use the default connection
     */
    private Connection connection;

    /**
     * Can be null, if null, use the default dbType
     */
    private String dbType;
}
