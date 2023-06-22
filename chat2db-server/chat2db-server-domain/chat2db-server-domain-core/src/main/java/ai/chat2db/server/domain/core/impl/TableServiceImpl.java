package ai.chat2db.server.domain.core.impl;

import java.util.List;

import ai.chat2db.server.domain.api.service.TableService;
import ai.chat2db.server.domain.support.dialect.MetaSchema;
import ai.chat2db.server.domain.support.enums.DbTypeEnum;
import ai.chat2db.server.domain.support.model.Sql;
import ai.chat2db.server.domain.support.model.Table;
import ai.chat2db.server.domain.support.model.TableColumn;
import ai.chat2db.server.domain.support.model.TableIndex;
import ai.chat2db.server.domain.api.param.DropParam;
import ai.chat2db.server.domain.api.param.ShowCreateTableParam;
import ai.chat2db.server.domain.api.param.TablePageQueryParam;
import ai.chat2db.server.domain.api.param.TableQueryParam;
import ai.chat2db.server.domain.api.param.TableSelector;
import ai.chat2db.server.domain.support.sql.DbhubContext;
import ai.chat2db.server.domain.support.util.SqlUtils;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import ai.chat2db.server.tools.common.util.EasyEnumUtils;

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
        MetaSchema metaSchema = DbhubContext.getConnectInfo().getDbType().metaSchema();
        String ddl = metaSchema.tableDDL(param.getDatabaseName(), param.getSchemaName(), param.getTableName());
        return DataResult.of(ddl);
    }

    @Override
    public ActionResult drop(DropParam param) {
        MetaSchema metaSchema = DbhubContext.getConnectInfo().getDbType().metaSchema();
        metaSchema.dropTable(param.getDatabaseName(), param.getTableSchema(), param.getTableName());
        return ActionResult.isSuccess();
    }

    @Override
    public DataResult<String> createTableExample(String dbType) {
        DbTypeEnum dbTypeEnum = EasyEnumUtils.getEnum(DbTypeEnum.class, dbType);
        return DataResult.of(dbTypeEnum.example().getCreateTable());
    }

    @Override
    public DataResult<String> alterTableExample(String dbType) {
        DbTypeEnum dbTypeEnum = EasyEnumUtils.getEnum(DbTypeEnum.class, dbType);
        return DataResult.of(dbTypeEnum.example().getAlterTable());
    }

    @Override
    public DataResult<Table> query(TableQueryParam param, TableSelector selector) {
        MetaSchema metaSchema = DbhubContext.getConnectInfo().getDbType().metaSchema();
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
        MetaSchema metaSchema = DbhubContext.getMetaSchema();
        List<Table> list = metaSchema.tables(param.getDatabaseName(), param.getSchemaName(), param.getTableName());
        if (CollectionUtils.isEmpty(list)) {
            return PageResult.of(list, 0L, param);
        }
        return PageResult.of(list, Long.valueOf(list.size()), param);
    }

    @Override
    public List<TableColumn> queryColumns(TableQueryParam param) {
        MetaSchema metaSchema = DbhubContext.getMetaSchema();
        return metaSchema.columns(param.getDatabaseName(), param.getSchemaName(), param.getTableName(),null);
    }

    @Override
    public List<TableIndex> queryIndexes(TableQueryParam param) {
        MetaSchema metaSchema = DbhubContext.getMetaSchema();
        return metaSchema.indexes(param.getDatabaseName(), param.getSchemaName(), param.getTableName());

    }
}
