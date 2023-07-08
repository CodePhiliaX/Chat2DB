package ai.chat2db.server.web.api.controller.data.source.request;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author moji
 * @version MysqlBaseRequest.java, v 0.1 2022年09月18日 11:51 moji Exp $
 * @date 2022/09/18
 */
@Data
public class DataSourceBaseRequest implements DataSourceBaseRequestInfo{

    /**
     * 数据源id
     */
    @NotNull
    private Long dataSourceId;

    /**
     * DB名称
     */
    private String databaseName;

    /**
     * 表所在空间
     */
    private String schemaName;


    /**
     * if true, refresh the cache
     */
    private boolean refresh;
}
