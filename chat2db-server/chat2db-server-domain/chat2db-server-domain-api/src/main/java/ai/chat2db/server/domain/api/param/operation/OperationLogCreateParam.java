package ai.chat2db.server.domain.api.param.operation;

import lombok.Data;

/**
 * @author moji
 * @version UserExecutedDdlCreateParam.java, v 0.1 September 25, 2022 11:08 moji Exp $
 * @date 2022/09/25
 */
@Data
public class OperationLogCreateParam {

    /**
     * primary key
     */
    private Long id;

    /**
     * Data source connection ID
     */
    private Long dataSourceId;

    /**
     * databaseName
     */
    private String databaseName;

    /**
     * Database type
     */
    private String type;

    /**
     * ddl content
     */
    private String ddl;


    /**
     * state
     */
    private String status;

    /**
     * Number of operation lines
     */
    private Long operationRows;

    /**
     * Length of use
     */
    private Long useTime;

    /**
     * Extended Information
     */
    private String extendInfo;

    /**
     * schema name
     */
    private String schemaName;
}
