package ai.chat2db.server.web.api.controller.operation.log.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author moji
 * @version DdlVO.java, v 0.1 2022年09月18日 11:06 moji Exp $
 * @date 2022/09/18
 */
@Data
public class OperationLogVO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtModified;

    /**
     * 文件别名
     */
    private String name;

    /**
     * 数据源id
     */
    private Long dataSourceId;

    /**
     * 数据源名称
     */
    private String dataSourceName;

    /**
     * 是否可连接
     */
    private Boolean connectable;

    /**
     * DB名称
     */
    private String databaseName;

    /**
     * ddl语言类型
     */
    private String type;

    /**
     * ddl内容
     */
    private String ddl;

    /**
     * 状态
     */
    private String status;

    /**
     * 操作行数
     */
    private Long operationRows;

    /**
     * 使用时长
     */
    private Long useTime;

    /**
     * 扩展信息
     */
    private String extendInfo;

    /**
     * schema名称
     */
    private String schemaName;
}
