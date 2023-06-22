package ai.chat2db.server.domain.core.impl;

import java.util.List;

import ai.chat2db.server.domain.api.param.DatabaseOperationParam;
import ai.chat2db.server.domain.api.param.SchemaOperationParam;
import ai.chat2db.server.domain.api.service.DatabaseService;
import ai.chat2db.spi.model.Database;
import ai.chat2db.spi.model.Schema;
import ai.chat2db.server.domain.api.param.DatabaseQueryAllParam;
import ai.chat2db.server.domain.api.param.SchemaQueryParam;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.tools.common.util.EasyCollectionUtils;

import ai.chat2db.spi.sql.Chat2DBContext;
import org.springframework.stereotype.Service;

/**
 * @author moji
 * @version DataSourceCoreServiceImpl.java, v 0.1 2022年09月23日 15:51 moji Exp $
 * @date 2022/09/23
 */
@Service
public class DatabaseServiceImpl implements DatabaseService {

    @Override
    public ListResult<Database> queryAll(DatabaseQueryAllParam param) {
        List<String> databases = Chat2DBContext.getMetaData().databases();
        return ListResult.of(EasyCollectionUtils.toList(databases, name -> Database.builder().name(name).build()));
    }

    @Override
    public ListResult<Schema> querySchema(SchemaQueryParam param) {
        List<String> databases = Chat2DBContext.getMetaData().schemas(param.getDataBaseName());
        return ListResult.of(EasyCollectionUtils.toList(databases, name -> Schema.builder().name(name).build()));
    }

    @Override
    public ActionResult deleteDatabase(DatabaseOperationParam param) {
        Chat2DBContext.getDBManage().dropDatabase(param.getDatabaseName());
        return ActionResult.isSuccess();
    }

    @Override
    public ActionResult createDatabase(DatabaseOperationParam param) {
        Chat2DBContext.getDBManage().createDatabase(param.getDatabaseName());
        return ActionResult.isSuccess();
    }

    @Override
    public ActionResult modifyDatabase(DatabaseOperationParam param) {
        Chat2DBContext.getDBManage().modifyDatabase(param.getDatabaseName(),param.getNewDatabaseName());
        return ActionResult.isSuccess();
    }

    @Override
    public ActionResult deleteSchema(SchemaOperationParam param) {
        Chat2DBContext.getDBManage().dropSchema(param.getDatabaseName(),param.getSchemaName());
        return ActionResult.isSuccess();
    }

    @Override
    public ActionResult createSchema(SchemaOperationParam param) {
        Chat2DBContext.getDBManage().createSchema(param.getDatabaseName(),param.getSchemaName());
        return ActionResult.isSuccess();
    }

    @Override
    public ActionResult modifySchema(SchemaOperationParam param) {
        Chat2DBContext.getDBManage().modifySchema(param.getDatabaseName(),param.getSchemaName(),
            param.getNewSchemaName());
        return ActionResult.isSuccess();
    }

}
