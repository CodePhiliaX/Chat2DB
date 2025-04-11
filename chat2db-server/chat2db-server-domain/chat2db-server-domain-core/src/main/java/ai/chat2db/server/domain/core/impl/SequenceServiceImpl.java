package ai.chat2db.server.domain.core.impl;


import ai.chat2db.server.domain.api.param.SequencePageQueryParam;
import ai.chat2db.server.domain.api.param.ShowCreateSequenceParam;
import ai.chat2db.server.domain.api.service.SequenceService;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.model.SimpleSequence;
import ai.chat2db.spi.sql.Chat2DBContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Sequence source management serviceImpl
 *
 * @author Sylphy
 */
@Slf4j
@Service
public class SequenceServiceImpl implements SequenceService {
    @Override
    public DataResult<String> showCreateSequence(ShowCreateSequenceParam param) {
        MetaData metaSchema = Chat2DBContext.getMetaData();
        String ddl = metaSchema.sequenceDDL(Chat2DBContext.getConnection(), param.getDatabaseName(), param.getSchemaName(), param.getSequenceName());
        return DataResult.of(ddl);
    }

    public ListResult<SimpleSequence> pageQuery(SequencePageQueryParam request) {
        MetaData metaSchema = Chat2DBContext.getMetaData();
        List<SimpleSequence> sequences = metaSchema.sequences(Chat2DBContext.getConnection(), request.getDatabaseName(), request.getSchemaName());
        return ListResult.of(sequences);
    }
}
