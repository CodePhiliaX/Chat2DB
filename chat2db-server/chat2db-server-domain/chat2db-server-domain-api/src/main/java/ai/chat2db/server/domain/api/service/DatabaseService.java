package ai.chat2db.server.domain.api.service;

import ai.chat2db.server.domain.api.param.*;
import ai.chat2db.server.domain.api.param.datasource.DatabaseCreateParam;
import ai.chat2db.server.domain.api.param.datasource.DatabaseQueryAllParam;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.spi.model.*;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;

/**
 * 数据源管理服务
 *
 * @author moji
 * @version DataSourceCoreService.java, v 0.1 2022年09月23日 15:22 moji Exp $
 * @date 2022/09/23
 */
public interface DatabaseService {

    /**
     * 查询数据源下的所有database
     *
     * @param param
     * @return
     */
    ListResult<Database> queryAll(DatabaseQueryAllParam param);

    /**
     * 查询某个database下的schema
     * @param param
     * @return
     */
    ListResult<Schema> querySchema(SchemaQueryParam param);

    /**
     * query Database and Schema
     * @param param
     * @return
     */
    DataResult<MetaSchema> queryDatabaseSchema(MetaDataQueryParam param);



    /**
     * 删除数据库
     *
     * @param param
     * @return
     */
    ActionResult deleteDatabase(DatabaseCreateParam param);

    /**
     * 创建database
     *
     * @param param
     * @return
     */
    DataResult<Sql> createDatabase(Database param);

    /**
     * 修改database
     *
     * @return
     */
    ActionResult modifyDatabase( DatabaseCreateParam param) ;

    /**
     * 删除schema
     *
     * @param param
     * @return
     */
    ActionResult deleteSchema(SchemaOperationParam param) ;

    /**
     * 创建schema
     *
     * @param schema
     * @return
     */
    DataResult<Sql> createSchema(Schema schema);

    /**
     * 修改schema
     *
     * @param request
     * @return
     */
    ActionResult modifySchema( SchemaOperationParam request);
}
