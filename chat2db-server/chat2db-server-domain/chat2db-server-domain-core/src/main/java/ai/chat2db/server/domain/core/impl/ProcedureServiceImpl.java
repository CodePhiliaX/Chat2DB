package ai.chat2db.server.domain.core.impl;

import ai.chat2db.server.domain.api.service.ProcedureService;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.spi.model.Procedure;
import ai.chat2db.spi.sql.Chat2DBContext;
import org.springframework.stereotype.Service;

@Service
public class ProcedureServiceImpl implements ProcedureService {

    @Override
    public ListResult<Procedure> procedures(String databaseName, String schemaName) {
        return ListResult.of(Chat2DBContext.getMetaData().procedures(Chat2DBContext.getConnection(),databaseName, schemaName));
    }

    @Override
    public DataResult<Procedure> detail(String databaseName, String schemaName, String procedureName) {
        return DataResult.of(Chat2DBContext.getMetaData().procedure(Chat2DBContext.getConnection(), databaseName, schemaName, procedureName));
    }
}
