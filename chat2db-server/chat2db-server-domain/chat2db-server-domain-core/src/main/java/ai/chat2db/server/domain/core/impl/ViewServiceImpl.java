package ai.chat2db.server.domain.core.impl;

import ai.chat2db.server.domain.api.service.ViewService;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.sql.Chat2DBContext;
import org.springframework.stereotype.Service;

@Service
public class ViewServiceImpl implements ViewService {

    @Override
    public ListResult<Table> views(String databaseName, String schemaName) {
        return ListResult.of(Chat2DBContext.getMetaData().views(Chat2DBContext.getConnection(),databaseName, schemaName));
    }

    @Override
    public DataResult<Table> detail(String databaseName, String schemaName, String tableName) {
        MetaData metaSchema = Chat2DBContext.getMetaData();
        String ddl = metaSchema.tableDDL(Chat2DBContext.getConnection(), databaseName, schemaName, tableName);
        Table table = new Table();
        table.setDdl(ddl);
        table.setName(tableName);
        table.setSchemaName(schemaName);
        table.setDatabaseName(databaseName);
        return DataResult.of(table);
    }

}
