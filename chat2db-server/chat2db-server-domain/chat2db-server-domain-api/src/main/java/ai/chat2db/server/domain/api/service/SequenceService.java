package ai.chat2db.server.domain.api.service;

import ai.chat2db.server.domain.api.param.DropParam;
import ai.chat2db.server.domain.api.param.SequencePageQueryParam;
import ai.chat2db.server.domain.api.param.SequenceQueryParam;
import ai.chat2db.server.domain.api.param.ShowCreateSequenceParam;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.spi.model.Sequence;
import ai.chat2db.spi.model.SimpleSequence;
import ai.chat2db.spi.model.Sql;

/**
 * Sequence source management services
 *
 * @author Sylphy
 */
public interface SequenceService {
    DataResult<String> showCreateSequence(ShowCreateSequenceParam request);

    ListResult<SimpleSequence> pageQuery(SequencePageQueryParam request);

    ListResult<Sql> buildSql(Sequence oldSequence, Sequence newSequence);

    ActionResult drop(DropParam dropParam);

    DataResult<Sequence> query(SequenceQueryParam queryParam);
}
