package ai.chat2db.server.web.api.controller.rdb.request;

import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class SchemaCreateRequest extends DataSourceBaseRequest {

    /**
     * 数据名字
     */
    @JsonAlias({"TABLE_SCHEM"})
    private String name;


    private String comment;


    private String owner;
}
