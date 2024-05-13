package ai.chat2db.server.domain.api.param;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Console creation parameters
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ConsoleCreateParam {
    /**
     * Corresponding source id stored in the database
     */
    @NotNull
    private Long dataSourceId;

    /**
     * The id of the console, ensuring global uniqueness
     * Make sure not to duplicate it, in which case the previous connection will be discarded and recreated
     */
    @NotNull
    private Long consoleId;

    /**
     * Corresponding connection database name
     * Databases that support multiple databases will call use xx; to switch to the database.
     */
    @NotNull
    private String databaseName;
}
