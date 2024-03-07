package ai.chat2db.server.domain.api.service;

import java.util.List;

import ai.chat2db.server.domain.api.param.*;
import ai.chat2db.spi.model.*;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;

/**
 * Data source management services
 *
 * @author moji
 * @version DataSourceCoreService.java, v 0.1 September 23, 2022 15:22 moji Exp $
 * @date 2022/09/23
 */
public interface TableService {

    /**
     * Query table information
     *
     * @param param
     * @return
     */
    DataResult<String> showCreateTable(ShowCreateTableParam param);

    /**
     * Delete table
     *
     * @param param
     * @return
     */
    ActionResult drop(DropParam param);

    /**
     * Example of creating a table structure
     *
     * @param dbType
     * @return
     */
    DataResult<String> createTableExample(String dbType);

    /**
     * Example of modifying table structure
     *
     * @param dbType
     * @return
     */
    DataResult<String> alterTableExample(String dbType);

    /**
     * Query table information
     *
     * @param param
     * @return
     */
    DataResult<Table> query(TableQueryParam param, TableSelector selector);

    /**
     * build sql
     *
     * @param oldTable
     * @param newTable
     * @return
     */
    ListResult<Sql> buildSql(Table oldTable, Table newTable);

    /**
     * Pagination query table information
     *
     * @param param
     * @return
     */
    PageResult<Table> pageQuery(TablePageQueryParam param, TableSelector selector);


    /**
     * Query table information
     * @param param
     * @return
     */
    ListResult<SimpleTable> queryTables(TablePageQueryParam param);

    /**
     * Fields included in the query table
     *
     * @param param
     * @return
     */
    List<TableColumn> queryColumns(TableQueryParam param);

    /**
     * Query table index
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
     * save table vector
     *
     * @param param
     * @return
     */
    ActionResult saveTableVector(TableVectorParam param);

    /**
     * check if table vector saved status
     *
     * @param param
     * @return
     */
    DataResult<Boolean> checkTableVector(TableVectorParam param);


    /**
     * Get dml template sql
     * @param param table query param
     * @return sql
     */
    DataResult<String> copyDmlSql(DmlSqlCopyParam param);
}
