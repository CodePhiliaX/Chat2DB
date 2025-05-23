package ai.chat2db.server.web.api.controller.rdb.request;


import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Modify sequence sql request
 *
 * @author Sylphy
 */
@Data
public class SequenceModifySqlRequest extends DataSourceBaseRequest {
    /**
     * Old sequence structure
     * Empty means creating a new sequence
     */
    private SequenceRequest oldSequence;
    /**
     * new sequence structure
     */
    @NotNull
    private SequenceRequest newSequence;
}
