package ai.chat2db.server.domain.api.service;

import ai.chat2db.server.domain.api.param.SequencePageQueryParam;
import ai.chat2db.server.domain.api.param.ShowCreateSequenceParam;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.spi.model.SimpleSequence;

/**
 * Sequence source management services
 *
 * @author Sylphy
 */
public interface SequenceService {
    DataResult<String> showCreateSequence(ShowCreateSequenceParam request);

    ListResult<SimpleSequence> pageQuery(SequencePageQueryParam request);
}
