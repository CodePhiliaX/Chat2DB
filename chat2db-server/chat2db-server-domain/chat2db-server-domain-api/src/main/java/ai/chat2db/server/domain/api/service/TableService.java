package ai.chat2db.server.domain.api.service;

import java.util.List;

import ai.chat2db.server.domain.api.model.TreeNode;
import ai.chat2db.server.domain.api.param.CreateVirtualFKParam;
import ai.chat2db.server.domain.api.param.DeprecatedTableParam;
import ai.chat2db.server.domain.api.param.DropKeyParam;
import ai.chat2db.server.domain.api.param.DropParam;
import ai.chat2db.server.domain.api.param.ShowCreateTableParam;
import ai.chat2db.server.domain.api.param.TablePageQueryParam;
import ai.chat2db.server.domain.api.param.TableQueryParam;
import ai.chat2db.server.domain.api.param.TableSelector;
import ai.chat2db.server.domain.api.param.TreeSearchParam;
import ai.chat2db.server.domain.api.param.TypeQueryParam;
import ai.chat2db.server.domain.api.param.UpdateVirtualFKParam;
import ai.chat2db.server.domain.api.service.ForeignKeySyncService;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import ai.chat2db.spi.model.ExecuteResult;
import ai.chat2db.spi.model.ForeignKey;
import ai.chat2db.spi.model.SimpleTable;
import ai.chat2db.spi.model.Sql;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.model.TableColumn;
import ai.chat2db.spi.model.TableIndex;
import ai.chat2db.spi.model.TableMeta;
import ai.chat2db.spi.model.Type;
import ai.chat2db.spi.model.VirtualForeignKey;

/**
 * 数据源管理服务
 *
 * @author moji
 * @version DataSourceCoreService.java, v 0.1 2022年09月23日 15:22 moji Exp $
 * @date 2022/09/23
 */
public interface TableService {

    /**
     * 查询表信息
     *
     * @param param
     * @return
     */
    DataResult<String> showCreateTable(ShowCreateTableParam param);

    /**
     * 删除表
     *
     * @param param
     * @return
     */
    ActionResult drop(DropParam param);


    /**
     * 截断表
     *
     * @param param
     * @return
     */
    ActionResult truncate(DropParam param);

    /**
     * 创建表结构的样例
     *
     * @param dbType
     * @return
     */
    DataResult<String> createTableExample(String dbType);

    /**
     * 修改表结构的样例
     *
     * @param dbType
     * @return
     */
    DataResult<String> alterTableExample(String dbType);

    /**
     * 查询表信息
     *
     * @param param
     * @return
     */
    DataResult<Table> query(TableQueryParam param, TableSelector selector);

    /**
     * 构建sql
     *
     * @param oldTable
     * @param newTable
     * @return
     */
    ListResult<Sql> buildSql(Table oldTable, Table newTable);
    /**
     * 批量生成sql
     * @param oldTables
     * @param newTables
     * @return
     */
    ListResult<String> buildBatchSql(List<Table> oldTables, List<Table> newTables);

    /**
     * 分页查询表信息
     *
     * @param param
     * @return
     */
    PageResult<Table> pageQuery(TablePageQueryParam param, TableSelector selector);

    /**
     * 分页查询已废弃的表信息（回收站）
     *
     * @param param
     * @return
     */
    PageResult<Table> pageQueryDeprecated(TablePageQueryParam param, TableSelector selector);


    /**
     * 查询表信息
     * @param param
     * @return
     */
    ListResult<SimpleTable> queryTables(TablePageQueryParam param);

    /**
     * 查询表包含的字段
     *
     * @param param
     * @return
     */
    List<TableColumn> queryColumns(TableQueryParam param);

    /**
     * 查询表索引
     *
     * @param param
     * @return
     */
    List<TableIndex> queryIndexes(TableQueryParam param);

    /**
     *
     * @param param
     *
     * @return
     */
    List<Type> queryTypes(TypeQueryParam param);

    /**
     *
     * @param param
     * @return
     */
    TableMeta queryTableMeta(TypeQueryParam param);


    /**
     * Search tree nodes for tables.
     *
     * @param param
     * @return
     */
    List<TreeNode> searchTreeNodes(TreeSearchParam param);

    /**
     * Deprecated table
     * @param param
     * @return
     */
    ActionResult deprecatedTable(DeprecatedTableParam param);

    /**
     * Delete deprecated table
     * @param param
     * @return
     */
    ActionResult deleteDeprecatedTable(DeprecatedTableParam param);

    /**
     * Query user deprecated tables
     * @param param
     * @return
     */
    ListResult<String> queryDeprecatedTables(DeprecatedTableParam param);

    /**
     * Batch optimize tables
     * @param tableNames
     * @param databaseName
     * @param schemaName
     * @return
     */
    ListResult<ExecuteResult> batchOptimizeTables(List<String> tableNames, String databaseName, String schemaName);

    /**
     * Batch analyze tables
     * @param tableNames
     * @param databaseName
     * @param schemaName
     * @return
     */
    ListResult<ExecuteResult> batchAnalyzeTables(List<String> tableNames, String databaseName, String schemaName);

}
