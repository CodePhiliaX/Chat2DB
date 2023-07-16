package ai.chat2db.server.domain.core.impl;

import ai.chat2db.server.domain.api.service.ProcedureService;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.spi.model.Procedure;
import ai.chat2db.spi.sql.Chat2DBContext;
import org.springframework.stereotype.Service;

@Service
public class ProcedureServiceImpl implements ProcedureService {

    @Override
    public ListResult<Procedure> procedures(String databaseName, String schemaName) {
        return ListResult.of(Chat2DBContext.getMetaData().procedures(databaseName, schemaName));
    }
}
