package ai.chat2db.server.domain.api.service;

import ai.chat2db.server.domain.api.param.ShowCreateSequenceParam;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;

/**
 * Sequence source management services
 *
 * @author Sylphy
 */
public interface SequenceService {
    DataResult<String> showCreateSequence(ShowCreateSequenceParam request);
}
