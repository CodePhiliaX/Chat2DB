package ai.chat2db.server.domain.core.impl;

import ai.chat2db.server.domain.api.service.TriggerService;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.spi.model.Trigger;
import ai.chat2db.spi.sql.Chat2DBContext;
import org.springframework.stereotype.Service;

@Service
public class TriggerServiceImpl implements TriggerService {
    @Override
    public ListResult<Trigger> triggers(String databaseName, String schemaName) {
        return ListResult.of(Chat2DBContext.getMetaData().triggers(databaseName, schemaName));
    }
}
