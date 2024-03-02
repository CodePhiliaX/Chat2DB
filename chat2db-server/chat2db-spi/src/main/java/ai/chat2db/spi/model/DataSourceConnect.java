package ai.chat2db.spi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Database connection object
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceConnect {

    /**
     * success flag
     */
    private Boolean success;

    /**
     * Failure message prompt
     * Only in case of failure
     */
    private String message;

    /**
     * description
     */
    private String description;

    /**
     * error detail
     */
    private String errorDetail;
}
