package ai.chat2db.server.web.api.controller.data.source.request;


import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author moji
 * @version ConnectionCreateRequest.java, v 0.1 September 16, 2022 14:23 moji Exp $
 * @date 2022/09/16
 */
@Data
public class DataSourceAttachRequest implements DataSourceBaseRequestInfo{

    /**
     * primary key id
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
