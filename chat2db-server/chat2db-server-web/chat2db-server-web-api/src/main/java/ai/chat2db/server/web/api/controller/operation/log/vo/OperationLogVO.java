package ai.chat2db.server.web.api.controller.operation.log.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author moji
 * @version DdlVO.java, v 0.1 September 18, 2022 11:06 moji Exp $
 * @date 2022/09/18
 */
@Data
public class OperationLogVO {

    /**
     * primary key
     */
    private Long id;

    /**
     * creation time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreate;

    /**
     * modified time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtModified;

    /**
     * file alias
     */
    private String name;

    /**
     * Data source id
     */
    private Long dataSourceId;

    /**
     * Data source name
     */
    private String dataSourceName;

    /**
     * Is it connectable?
     */
    private Boolean connectable;

    /**
     * DB name
     */
    private String databaseName;

    /**
     * ddl language type
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
