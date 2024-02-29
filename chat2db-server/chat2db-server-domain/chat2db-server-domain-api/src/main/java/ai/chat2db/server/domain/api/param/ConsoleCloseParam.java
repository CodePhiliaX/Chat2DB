package ai.chat2db.server.domain.api.param;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Console shutdown parameters
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ConsoleCloseParam {

    /**
     * Corresponding source id stored in the database
     */
    @NotNull
    private Long dataSourceId;

    /**
     * The id of the console, ensuring global uniqueness
     */
    @NotNull
    private Long consoleId;
}
