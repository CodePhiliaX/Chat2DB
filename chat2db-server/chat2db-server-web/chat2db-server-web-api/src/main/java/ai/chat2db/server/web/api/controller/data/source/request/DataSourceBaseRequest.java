package ai.chat2db.server.web.api.controller.data.source.request;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author moji
 * @version MysqlBaseRequest.java, v 0.1 September 18, 2022 11:51 moji Exp $
 * @date 2022/09/18
 */
@Data
public class DataSourceBaseRequest implements DataSourceBaseRequestInfo{

    /**
     * Data source id
     */
    @NotNull
    private Long dataSourceId;

    /**
     * DB name
     */
    private String databaseName;

    /**
     * The space where the table is located
     */
    private String schemaName;


    /**
     * if true, refresh the cache
     */
    private boolean refresh;
}
