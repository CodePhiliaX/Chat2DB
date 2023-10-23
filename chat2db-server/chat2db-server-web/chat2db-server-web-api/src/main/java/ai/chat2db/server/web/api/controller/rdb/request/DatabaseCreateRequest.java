package ai.chat2db.server.web.api.controller.rdb.request;

import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import lombok.Data;

@Data
public class DatabaseCreateRequest extends DataSourceBaseRequest {

    private String name;

    private String comment;

    private String charset;

    private String collation;
}
