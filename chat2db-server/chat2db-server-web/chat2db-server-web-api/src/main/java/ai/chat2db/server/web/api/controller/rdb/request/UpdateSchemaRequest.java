
package ai.chat2db.server.web.api.controller.rdb.request;

import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;

import lombok.Data;

/**
 * @author jipengfei
 * @version : UpdateSchemaRequest.java
 */
@Data
public class UpdateSchemaRequest extends DataSourceBaseRequest {

    private String newSchemaName;

}