package ai.chat2db.server.domain.api.service;

import java.util.List;

import ai.chat2db.server.domain.api.param.*;
import ai.chat2db.server.domain.api.param.datasource.DatabaseCreateParam;
import ai.chat2db.server.domain.api.param.datasource.DatabaseQueryAllParam;
import ai.chat2db.spi.model.*;

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
    List<Database> queryAll(DatabaseQueryAllParam param);

    /**
     * 查询某个database下的schema
     * @param param
     * @return
     */
    List<Schema> querySchema(SchemaQueryParam param);

    /**
     * query Database and Schema
     * @param param
     * @return
     */
    MetaSchema queryDatabaseSchema(MetaDataQueryParam param);



    /**
     * 删除数据库
     *
     * @param param
     * @return
     */
    void deleteDatabase(DatabaseCreateParam param);

    /**
     * 创建database
     *
     * @param param
     * @return
     */
    Sql createDatabase(Database param);

    /**
     * 修改database
     *
     * @return
     */
    void modifyDatabase( DatabaseCreateParam param) ;

    /**
     * 删除schema
     *
     * @param param
     * @return
     */
    void deleteSchema(SchemaOperationParam param) ;

    /**
     * 创建schema
     *
     * @param schema
     * @return
     */
    Sql createSchema(Schema schema);

    /**
     * 修改 schema
     *
     * @param request
     * @return
     */
    void modifySchema( SchemaOperationParam request);

    /**
     * 查询单个表的 DDL
     *
     * @param dataSourceId 数据源 ID
     * @param databaseName 数据库名称
     * @param schemaName Schema 名称
     * @param tableName 表名
     * @return 表的 DDL
     */
    String queryTableDdl(Long dataSourceId, String databaseName, String schemaName, String tableName);

    /**
     * 批量构建表列信息
     *
     * @param dataSourceId 数据源 ID
     * @param databaseName 数据库名称
     * @param schemaName Schema 名称
     * @param tableNames 表名列表
     * @return 表的 DDL 拼接结果
     */
    String buildTableColumn(Long dataSourceId, String databaseName, String schemaName, java.util.List<String> tableNames);

    /**
     * 查询数据库所有表信息（用于自动选表场景）
     *
     * @param dataSourceId 数据源 ID
     * @param databaseName 数据库名称
     * @param schemaName Schema 名称
     * @return 表的元数据信息（包含注释、外键等）
     */
    String queryDatabaseTables(Long dataSourceId, String databaseName, String schemaName);

    /**
     * 查询 Redis Schema 信息
     *
     * @param dataSourceId 数据源 ID
     * @param databaseName 数据库名称
     * @param schemaName Schema 名称
     * @param tableNames key 名称列表
     * @return Redis key 列表
     */
    String queryRedisSchema(Long dataSourceId, String databaseName, String schemaName, java.util.List<String> tableNames);


}
