package ai.chat2db.server.web.api.controller.rdb.request;


import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Delete sequence sql request
 *
 * @author Sylphy
 */
@Data
public class SequenceDeleteRequest extends DataSourceBaseRequest {
    /**
     * Sequence Name
     */
    @NotNull
    private String sequenceName;
}
