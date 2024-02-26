package ai.chat2db.server.web.api.controller.rdb.request;

import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: zgq
 * @date: 2024年02月24日 13:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcedureUpdateRequest extends DataSourceBaseRequest {

    private String procedureName;
    private String procedureBody;

}
