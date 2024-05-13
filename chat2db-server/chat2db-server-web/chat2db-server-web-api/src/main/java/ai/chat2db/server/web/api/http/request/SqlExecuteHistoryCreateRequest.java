package ai.chat2db.server.web.api.http.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * <p>
 * sql execution history
 * </p>
 *
 * @author chat2db
 * @since 2023-12-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SqlExecuteHistoryCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * Database type
     */
    private String databaseType;

    /**
     * Execute SQL
     */
    private String sqlContent;

    /**
     * Client ID
     */
    private String clientId;

    /**
     * state
     */
    private String executeStatus;

    /**
     * wrong information
     */
    private String errorMessage;

    /**
     * sql type
     */
    private String sqlType;

    /**
     * execution duration
     */
    private Long duration;

    /**
     * Table Name
     */
    private String tableName;
}
