package ai.chat2db.server.web.api.http.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * <p>
 * sql执行历史
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
     * 数据库类型
     */
    private String databaseType;

    /**
     * 执行SQL
     */
    private String sqlContent;

    /**
     * 客户端ID
     */
    private String clientId;

    /**
     * 状态
     */
    private String executeStatus;

    /**
     * 错误消息
     */
    private String errorMessage;

    /**
     * sql 类型
     */
    private String sqlType;

    /**
     * 执行持续时间
     */
    private Long duration;

    /**
     * 表名
     */
    private String tableName;
}
