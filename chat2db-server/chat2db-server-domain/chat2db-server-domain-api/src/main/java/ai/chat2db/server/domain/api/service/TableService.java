package ai.chat2db.server.domain.api.service;

import java.util.List;

import ai.chat2db.server.domain.support.model.Sql;
import ai.chat2db.server.domain.support.model.Table;
import ai.chat2db.server.domain.support.model.TableColumn;
import ai.chat2db.server.domain.support.model.TableIndex;
import ai.chat2db.server.domain.api.param.DropParam;
import ai.chat2db.server.domain.api.param.ShowCreateTableParam;
import ai.chat2db.server.domain.api.param.TablePageQueryParam;
import ai.chat2db.server.domain.api.param.TableQueryParam;
import ai.chat2db.server.domain.api.param.TableSelector;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;

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
     * 分页查询表信息
     *
     * @param param
     * @return
     */
    PageResult<Table> pageQuery(TablePageQueryParam param, TableSelector selector);

    /**
     * 查询表包含的字段
     * @param param
     * @return
     */
    List<TableColumn> queryColumns(TableQueryParam param);

    /**
     * 查询表索引
     * @param param
     * @return
     */
    List<TableIndex> queryIndexes(TableQueryParam param);
}
