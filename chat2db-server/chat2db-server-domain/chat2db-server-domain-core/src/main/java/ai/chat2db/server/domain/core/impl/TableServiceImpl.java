package ai.chat2db.server.domain.core.impl;

import java.util.List;

import ai.chat2db.server.domain.api.service.TableService;
import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.model.Sql;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.model.TableColumn;
import ai.chat2db.spi.model.TableIndex;
import ai.chat2db.server.domain.api.param.DropParam;
import ai.chat2db.server.domain.api.param.ShowCreateTableParam;
import ai.chat2db.server.domain.api.param.TablePageQueryParam;
import ai.chat2db.server.domain.api.param.TableQueryParam;
import ai.chat2db.server.domain.api.param.TableSelector;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import ai.chat2db.server.tools.common.util.EasyEnumUtils;

import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.util.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

/**
 * @author moji
 * @version DataSourceCoreServiceImpl.java, v 0.1 2022年09月23日 15:51 moji Exp $
 * @date 2022/09/23
 */
@Service
public class TableServiceImpl implements TableService {

    @Override
    public DataResult<String> showCreateTable(ShowCreateTableParam param) {
        MetaData metaSchema = Chat2DBContext.getMetaData();
        String ddl = metaSchema.tableDDL(param.getDatabaseName(), param.getSchemaName(), param.getTableName());
        return DataResult.of(ddl);
    }

    @Override
    public ActionResult drop(DropParam param) {
        DBManage metaSchema = Chat2DBContext.getDBManage();
        metaSchema.dropTable(param.getDatabaseName(), param.getTableSchema(), param.getTableName());
        return ActionResult.isSuccess();
    }

    @Override
    public DataResult<String> createTableExample(String dbType) {
        String sql = Chat2DBContext.getDBConfig().getSimpleCreateTable();
        return DataResult.of(sql);
    }

    @Override
    public DataResult<String> alterTableExample(String dbType) {
        String sql = Chat2DBContext.getDBConfig().getSimpleAlterTable();
        return DataResult.of(sql);
    }

    @Override
    public DataResult<Table> query(TableQueryParam param, TableSelector selector) {
        MetaData metaSchema = Chat2DBContext.getMetaData();
        List<Table> tables = metaSchema.tables(param.getDatabaseName(), param.getSchemaName(), param.getTableName());
        if (!CollectionUtils.isEmpty(tables)) {
            Table table = tables.get(0);
            table.setIndexList(
                    metaSchema.indexes(param.getDatabaseName(), param.getSchemaName(), param.getTableName()));
            table.setColumnList(
                    metaSchema.columns(param.getDatabaseName(), param.getSchemaName(), param.getTableName()));
            return DataResult.of(table);
        }
        return DataResult.of(null);
    }

    @Override
    public ListResult<Sql> buildSql(Table oldTable, Table newTable) {
        return ListResult.of(SqlUtils.buildSql(oldTable, newTable));
    }

    @Override
    public PageResult<Table> pageQuery(TablePageQueryParam param, TableSelector selector) {
        MetaData metaSchema = Chat2DBContext.getMetaData();
        List<Table> list = metaSchema.tables(param.getDatabaseName(), param.getSchemaName(), param.getTableName());
        if (CollectionUtils.isEmpty(list)) {
            return PageResult.of(list, 0L, param);
        }
        return PageResult.of(list, Long.valueOf(list.size()), param);
    }

    @Override
    public List<TableColumn> queryColumns(TableQueryParam param) {
        MetaData metaSchema = Chat2DBContext.getMetaData();
        return metaSchema.columns(param.getDatabaseName(), param.getSchemaName(), param.getTableName(), null);
    }

    @Override
    public List<TableIndex> queryIndexes(TableQueryParam param) {
        MetaData metaSchema = Chat2DBContext.getMetaData();
        return metaSchema.indexes(param.getDatabaseName(), param.getSchemaName(), param.getTableName());

    }
}
