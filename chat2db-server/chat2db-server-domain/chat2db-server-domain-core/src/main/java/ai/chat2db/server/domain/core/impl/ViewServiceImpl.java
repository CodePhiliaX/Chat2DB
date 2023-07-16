package ai.chat2db.server.domain.core.impl;

import ai.chat2db.server.domain.api.service.ViewService;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.sql.Chat2DBContext;
import org.springframework.stereotype.Service;

@Service
public class ViewServiceImpl implements ViewService {

    @Override
    public ListResult<Table> views(String databaseName, String schemaName) {
        return ListResult.of(Chat2DBContext.getMetaData().views(databaseName, schemaName));
    }
}
