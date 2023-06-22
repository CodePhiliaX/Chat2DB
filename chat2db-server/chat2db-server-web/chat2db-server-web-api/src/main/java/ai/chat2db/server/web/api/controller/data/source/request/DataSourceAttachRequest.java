package ai.chat2db.server.web.api.controller.data.source.request;


import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author moji
 * @version ConnectionCreateRequest.java, v 0.1 2022年09月16日 14:23 moji Exp $
 * @date 2022/09/16
 */
@Data
public class DataSourceAttachRequest implements DataSourceBaseRequestInfo{

    /**
     * 主键id
     */
    @NotNull
    private Long id;

    @Override
    public Long getDataSourceId() {
        return id;
    }

    @Override
    public String getDatabaseName() {
        return null;
    }
}
