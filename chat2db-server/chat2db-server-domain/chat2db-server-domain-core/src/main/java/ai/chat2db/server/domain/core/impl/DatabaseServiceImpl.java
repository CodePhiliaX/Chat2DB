package ai.chat2db.server.domain.core.impl;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import ai.chat2db.server.domain.api.param.DatabaseOperationParam;
import ai.chat2db.server.domain.api.param.DatabaseQueryAllParam;
import ai.chat2db.server.domain.api.param.MetaDataQueryParam;
import ai.chat2db.server.domain.api.param.SchemaOperationParam;
import ai.chat2db.server.domain.api.param.SchemaQueryParam;
import ai.chat2db.server.domain.api.service.DatabaseService;
import ai.chat2db.server.domain.core.cache.CacheManage;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.model.Database;
import ai.chat2db.spi.model.MetaSchema;
import ai.chat2db.spi.model.Schema;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.sql.ConnectInfo;
import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import static ai.chat2db.server.domain.core.cache.CacheKey.getDataSourceKey;

/**
 * @author moji
 * @version DataSourceCoreServiceImpl.java, v 0.1 2022年09月23日 15:51 moji Exp $
 * @date 2022/09/23
 */
@Slf4j
@Service
public class DatabaseServiceImpl implements DatabaseService {

    @Override
    public ListResult<Database> queryAll(DatabaseQueryAllParam param) {
        return ListResult.of(Chat2DBContext.getMetaData().databases());
    }

    @Override
    public ListResult<Schema> querySchema(SchemaQueryParam param) {
        return ListResult.of(Chat2DBContext.getMetaData().schemas(param.getDataBaseName()));
    }

    @Override
    public DataResult<MetaSchema> queryDatabaseSchema(MetaDataQueryParam param) {
        MetaSchema metaSchema = new MetaSchema();
        MetaData metaData = Chat2DBContext.getMetaData();
        ConnectInfo connectInfo = Chat2DBContext.getConnectInfo();
        MetaSchema ms = CacheManage.get(getDataSourceKey(param.getDataSourceId()), MetaSchema.class,
            (key) -> param.isRefresh(), (key) -> {
                List<Database> databases = metaData.databases();
                if (!CollectionUtils.isEmpty(databases)) {
                    // CountDownLatch countDownLatch = ThreadUtil.newCountDownLatch(databases.size());
                    for (Database database : databases) {
                        //ThreadUtil.execute(() -> {
                        try {
                           // Chat2DBContext.putContext(connectInfo);
                            database.setSchemas(metaData.schemas(database.getName()));
                            // countDownLatch.countDown();
                        } catch (Exception e) {
                            log.error("queryDatabaseSchema error", e);
                        } finally {
                            //Chat2DBContext.removeContext();
                        }
                        // });
                    }
                    metaSchema.setDatabases(databases);

                } else {
                    List<Schema> schemas = metaData.schemas(null);
                    metaSchema.setSchemas(schemas);
                }
                return metaSchema;
            });

        return DataResult.of(ms);
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
        Chat2DBContext.getDBManage().modifyDatabase(param.getDatabaseName(), param.getNewDatabaseName());
        return ActionResult.isSuccess();
    }

    @Override
    public ActionResult deleteSchema(SchemaOperationParam param) {
        Chat2DBContext.getDBManage().dropSchema(param.getDatabaseName(), param.getSchemaName());
        return ActionResult.isSuccess();
    }

    @Override
    public ActionResult createSchema(SchemaOperationParam param) {
        Chat2DBContext.getDBManage().createSchema(param.getDatabaseName(), param.getSchemaName());
        return ActionResult.isSuccess();
    }

    @Override
    public ActionResult modifySchema(SchemaOperationParam param) {
        Chat2DBContext.getDBManage().modifySchema(param.getDatabaseName(), param.getSchemaName(),
            param.getNewSchemaName());
        return ActionResult.isSuccess();
    }

}
