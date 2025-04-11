package ai.chat2db.server.domain.core.impl;


import ai.chat2db.server.domain.api.param.ShowCreateSequenceParam;
import ai.chat2db.server.domain.api.service.SequenceService;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.sql.Chat2DBContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Sequence source management serviceImpl
 *
 * @author: Sylphy
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
}
