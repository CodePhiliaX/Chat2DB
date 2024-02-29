package ai.chat2db.server.domain.api.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Task implements Serializable {
    /**
     * primary key
     */
    private Long id;

    /**
     * creation time
     */
    private Date gmtCreate;

    /**
     * modified time
     */
    private Date gmtModified;

    /**
     * Data source connection ID
     */
    private Long dataSourceId;

    /**
     * db name
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
     * Whether it has been deleted, y means deleted, n means not deleted
     */
    private String deleted;

    /**
     * user id
     */
    private Long userId;

    /**
     * task type, such as: DOWNLOAD_DATA, UPLOAD_TABLE_DATA, DOWNLOAD_TABLE_STRUCTURE, UPLOAD_TABLE_STRUCTURE,
     */
    private String taskType;

    /**
     * task status
     */
    private String taskStatus;

    /**
     * task progress
     */
    private String taskProgress;

    /**
     * task name
     */
    private String taskName;

    /**
     * download url
     */
    private String downloadUrl;

    /**
     * task content
     */
    private byte[] content;
}
