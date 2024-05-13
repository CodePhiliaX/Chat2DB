package ai.chat2db.server.domain.api.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class TaskCreateParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Data source connection ID
     */
    private Long dataSourceId;

    /**
     * databaseName
     */
    private String databaseName;

    /**
     * schema name
     */
    private String schemaName;

    /**
     * table_name
     */
    private String tableName;

    /**
     * user id
     */
    private Long userId;

    /**
     * task progress
     */
    private String taskProgress;

    /**
     * task name
     */
    private String taskName;

    /**
     * task type, such as: DOWNLOAD_DATA, UPLOAD_TABLE_DATA, DOWNLOAD_TABLE_STRUCTURE, UPLOAD_TABLE_STRUCTURE,
     */
    private String taskType;


}
