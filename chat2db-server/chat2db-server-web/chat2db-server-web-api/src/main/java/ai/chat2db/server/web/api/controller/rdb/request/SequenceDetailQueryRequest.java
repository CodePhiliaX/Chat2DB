package ai.chat2db.server.web.api.controller.rdb.request;


import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Query sequence detail request
 *
 * @author Sylphy
 */
@Data
public class SequenceDetailQueryRequest extends DataSourceBaseRequest {

    /**
     * Sequence Name
     */
    @NotNull
    private String sequenceName;

}
