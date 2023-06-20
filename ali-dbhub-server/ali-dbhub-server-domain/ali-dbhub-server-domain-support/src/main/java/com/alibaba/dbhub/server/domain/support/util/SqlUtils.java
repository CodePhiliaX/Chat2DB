/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alibaba.dbhub.server.domain.support.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.alibaba.dbhub.server.domain.support.enums.CollationEnum;
import com.alibaba.dbhub.server.domain.support.enums.IndexTypeEnum;
import com.alibaba.dbhub.server.domain.support.model.Sql;
import com.alibaba.dbhub.server.domain.support.model.Table;
import com.alibaba.dbhub.server.domain.support.model.TableColumn;
import com.alibaba.dbhub.server.domain.support.model.TableIndex;
import com.alibaba.dbhub.server.domain.support.model.TableIndexColumn;
import com.alibaba.dbhub.server.tools.common.util.EasyBooleanUtils;
import com.alibaba.dbhub.server.tools.common.util.EasyCollectionUtils;
import com.alibaba.dbhub.server.tools.common.util.EasyEnumUtils;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddColumn;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropColumnItem;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLColumnPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLCreateIndexStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropIndexStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLNotNullConstraint;
import com.alibaba.druid.sql.ast.statement.SQLNullConstraint;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUnique;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlCharExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableChangeColumn;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableModifyColumn;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlRenameTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlRenameTableStatement.Item;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlTableIndex;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author jipengfei
 * @version : SqlUtils.java
 */
public class SqlUtils {

    public static List<Sql> buildSql(Table oldTable, Table newTable) {
        List<Sql> sqlList = new ArrayList<>();
        // 创建表
        if (oldTable == null) {
            MySqlCreateTableStatement mySqlCreateTableStatement = new MySqlCreateTableStatement();
            mySqlCreateTableStatement.setDbType(DbType.mysql);
            mySqlCreateTableStatement.setTableName(newTable.getName());
            if (!Objects.isNull(newTable.getComment())) {
                mySqlCreateTableStatement.setComment(new MySqlCharExpr(newTable.getComment()));
            }
            List<TableColumn> columnList = newTable.getColumnList();
            if (!CollectionUtils.isEmpty(columnList)) {
                for (TableColumn tableColumn : columnList) {
                    SQLColumnDefinition sqlColumnDefinition = new SQLColumnDefinition();
                    mySqlCreateTableStatement.addColumn(sqlColumnDefinition);
                    sqlColumnDefinition.setName(tableColumn.getName());
                    sqlColumnDefinition.setDataType(new SQLDataTypeImpl(tableColumn.getColumnType()));
                    if (BooleanUtils.isNotFalse(tableColumn.getNullable())) {
                        sqlColumnDefinition.addConstraint(new SQLNullConstraint());
                    } else {
                        sqlColumnDefinition.addConstraint(new SQLNotNullConstraint());
                    }
                    if (!Objects.isNull(tableColumn.getDefaultValue())) {
                        sqlColumnDefinition.setDefaultExpr(new MySqlCharExpr(tableColumn.getDefaultValue()));
                    }
                    sqlColumnDefinition.setAutoIncrement(BooleanUtils.isTrue(tableColumn.getAutoIncrement()));
                    if (!Objects.isNull(tableColumn.getComment())) {
                        sqlColumnDefinition.setComment(tableColumn.getComment());
                    }
                    if (BooleanUtils.isTrue(tableColumn.getPrimaryKey())) {
                        sqlColumnDefinition.addConstraint(new SQLColumnPrimaryKey());
                    }
                }
                //// 主键
                //List<TableColumn> primaryKeyColumnList = EasyCollectionUtils.stream(columnList)
                //    .filter(tableColumn -> BooleanUtils.isTrue(tableColumn.getPrimaryKey()))
                //    .collect(Collectors.toList());
                //if (!CollectionUtils.isEmpty(primaryKeyColumnList)) {
                //    MySqlPrimaryKey mySqlPrimaryKey = new MySqlPrimaryKey();
                //    mySqlCreateTableStatement.getTableElementList().add(mySqlPrimaryKey);
                //    for (TableColumn tableColumn : primaryKeyColumnList) {
                //        mySqlPrimaryKey.addColumn(new SQLIdentifierExpr(tableColumn.getName()));
                //    }
                //}
            }

            // 索引
            List<TableIndex> indexList = newTable.getIndexList();
            if (!CollectionUtils.isEmpty(indexList)) {
                for (TableIndex tableIndex : indexList) {
                    if (IndexTypeEnum.UNIQUE.getCode().equals(tableIndex.getType())) {
                        MySqlUnique mySqlUnique = new MySqlUnique();
                        mySqlCreateTableStatement.getTableElementList().add(mySqlUnique);
                        mySqlUnique.setName(tableIndex.getName());
                        mySqlUnique.setComment(new SQLCharExpr(tableIndex.getComment()));
                        mySqlUnique.getIndexDefinition().setType("unique");
                        if (!CollectionUtils.isEmpty(tableIndex.getColumnList())) {
                            for (TableIndexColumn tableIndexColumn : tableIndex.getColumnList()) {
                                SQLSelectOrderByItem sqlSelectOrderByItem = new SQLSelectOrderByItem();
                                sqlSelectOrderByItem.setExpr(new SQLIdentifierExpr(tableIndexColumn.getColumnName()));
                                CollationEnum collation = EasyEnumUtils.getEnum(CollationEnum.class,
                                    tableIndexColumn.getCollation());
                                if (collation != null) {
                                    sqlSelectOrderByItem.setType(collation.getSqlOrderingSpecification());
                                }
                                mySqlUnique.addColumn(sqlSelectOrderByItem);
                            }
                        }
                    } else {
                        MySqlTableIndex mySqlTableIndex = new MySqlTableIndex();
                        mySqlCreateTableStatement.getTableElementList().add(mySqlTableIndex);
                        mySqlTableIndex.setName(tableIndex.getName());
                        mySqlTableIndex.setComment(new SQLCharExpr(tableIndex.getComment()));
                        if (!CollectionUtils.isEmpty(tableIndex.getColumnList())) {
                            for (TableIndexColumn tableIndexColumn : tableIndex.getColumnList()) {
                                SQLSelectOrderByItem sqlSelectOrderByItem = new SQLSelectOrderByItem();
                                sqlSelectOrderByItem.setExpr(new SQLIdentifierExpr(tableIndexColumn.getColumnName()));
                                CollationEnum collation = EasyEnumUtils.getEnum(CollationEnum.class,
                                    tableIndexColumn.getCollation());
                                if (collation != null) {
                                    sqlSelectOrderByItem.setType(collation.getSqlOrderingSpecification());
                                }
                                mySqlTableIndex.addColumn(sqlSelectOrderByItem);
                            }
                        }
                    }
                }
            }

            sqlList.add(Sql.builder().sql(mySqlCreateTableStatement + ";").build());
            return sqlList;
        }

        // 修改表结构
        // 修改表名字
        if (!StringUtils.equals(oldTable.getName(), newTable.getName())) {
            MySqlRenameTableStatement mySqlRenameTableStatement = new MySqlRenameTableStatement();
            mySqlRenameTableStatement.setDbType(DbType.mysql);
            Item item = new Item();
            item.setName(new SQLIdentifierExpr(oldTable.getName()));
            item.setTo(new SQLIdentifierExpr(newTable.getName()));
            mySqlRenameTableStatement.addItem(item);
            sqlList.add(Sql.builder().sql(mySqlRenameTableStatement + ";").build());
        }
        // 修改注释
        if (!StringUtils.equals(oldTable.getComment(), newTable.getComment())) {
            SQLAlterTableStatement sqlAlterTableStatement = new SQLAlterTableStatement();
            sqlAlterTableStatement.setDbType(DbType.mysql);
            SQLAssignItem sqlAssignItem = new SQLAssignItem();
            sqlAssignItem.setTarget(new SQLIdentifierExpr("COMMENT"));
            sqlAssignItem.setValue(new MySqlCharExpr(newTable.getComment()));
            sqlAlterTableStatement.getTableOptions().add(sqlAssignItem);
            sqlList.add(Sql.builder().sql(sqlAlterTableStatement + ";").build());
        }
        // 修改字段
        modifyColumn(sqlList, oldTable, newTable);

        // 修改索引
        modifyIndex(sqlList, oldTable, newTable);
        return sqlList;
    }

    private static void modifyColumn(List<Sql> sqlList, Table oldTable, Table newTable) {
        Map<String, TableColumn> oldColumnMap = EasyCollectionUtils.toIdentityMap(oldTable.getColumnList(),
            tableColumn -> {
                if (tableColumn.getOldName() != null) {
                    return tableColumn.getOldName();
                }
                return tableColumn.getName();
            });
        Map<String, TableColumn> newColumnMap = EasyCollectionUtils.toIdentityMap(newTable.getColumnList(),
            tableColumn -> {
                if (tableColumn.getOldName() != null) {
                    return tableColumn.getOldName();
                }
                return tableColumn.getName();
            });

        SQLAlterTableStatement sqlAlterTableStatement = new SQLAlterTableStatement();
        sqlAlterTableStatement.setDbType(DbType.mysql);
        sqlAlterTableStatement.setTableSource(new SQLIdentifierExpr(newTable.getName()));

        newColumnMap.forEach((newTableColumnName, newTableColumn) -> {
            TableColumn oldTableColumn = oldColumnMap.get(newTableColumnName);
            // 代表新增字段
            if (oldTableColumn == null) {

                SQLAlterTableAddColumn sqlAlterTableAddColumn = new SQLAlterTableAddColumn();
                sqlAlterTableStatement.addItem(sqlAlterTableAddColumn);
                SQLColumnDefinition sqlColumnDefinition = new SQLColumnDefinition();
                sqlAlterTableAddColumn.addColumn(sqlColumnDefinition);
                sqlColumnDefinition.setName(newTableColumn.getName());
                sqlColumnDefinition.setDataType(new SQLDataTypeImpl(newTableColumn.getColumnType()));
                if (BooleanUtils.isNotTrue(newTableColumn.getNullable())) {
                    sqlColumnDefinition.addConstraint(new SQLNotNullConstraint());
                }
                if (!Objects.isNull(newTableColumn.getDefaultValue())) {
                    sqlColumnDefinition.setDefaultExpr(new MySqlCharExpr(newTableColumn.getDefaultValue()));
                }
                sqlColumnDefinition.setAutoIncrement(BooleanUtils.isTrue(newTableColumn.getAutoIncrement()));
                if (!Objects.isNull(newTableColumn.getComment())) {
                    sqlColumnDefinition.setComment(newTableColumn.getComment());
                }
                return;
            }
            // 代表可能修改字段 或者没变
            boolean hasChange = !StringUtils.equals(oldTableColumn.getName(), newTableColumn.getName())
                || !StringUtils.equals(oldTableColumn.getColumnType(), newTableColumn.getColumnType())
                || !EasyBooleanUtils.equals(oldTableColumn.getNullable(), newTableColumn.getNullable(), Boolean.TRUE)
                || !StringUtils.equals(oldTableColumn.getDefaultValue(), newTableColumn.getDefaultValue())
                || !EasyBooleanUtils.equals(oldTableColumn.getAutoIncrement(), newTableColumn.getAutoIncrement(),
                Boolean.FALSE)
                || !StringUtils.equals(oldTableColumn.getComment(), newTableColumn.getComment());

            // 没有修改字段
            if (!hasChange) {
                return;
            }

            // 修改字段包含字段名
            if (!StringUtils.equals(oldTableColumn.getName(), newTableColumn.getName())) {
                MySqlAlterTableChangeColumn mySqlAlterTableChangeColumn = new MySqlAlterTableChangeColumn();
                sqlAlterTableStatement.addItem(mySqlAlterTableChangeColumn);
                mySqlAlterTableChangeColumn.setColumnName(new SQLIdentifierExpr(newTableColumn.getOldName()));
                SQLColumnDefinition sqlColumnDefinition = new SQLColumnDefinition();
                mySqlAlterTableChangeColumn.setNewColumnDefinition(sqlColumnDefinition);
                sqlColumnDefinition.setName(newTableColumn.getName());
                sqlColumnDefinition.setDataType(new SQLDataTypeImpl(newTableColumn.getColumnType()));
                if (BooleanUtils.isNotTrue(newTableColumn.getNullable())) {
                    sqlColumnDefinition.addConstraint(new SQLNotNullConstraint());
                }
                if (!Objects.isNull(newTableColumn.getDefaultValue())) {
                    sqlColumnDefinition.setDefaultExpr(new MySqlCharExpr(newTableColumn.getDefaultValue()));
                }
                sqlColumnDefinition.setAutoIncrement(BooleanUtils.isTrue(newTableColumn.getAutoIncrement()));
                if (!Objects.isNull(newTableColumn.getComment())) {
                    sqlColumnDefinition.setComment(newTableColumn.getComment());
                }
            } else {
                // 修改字段不包括字段名
                MySqlAlterTableModifyColumn mySqlAlterTableModifyColumn = new MySqlAlterTableModifyColumn();
                sqlAlterTableStatement.addItem(mySqlAlterTableModifyColumn);
                SQLColumnDefinition sqlColumnDefinition = new SQLColumnDefinition();
                mySqlAlterTableModifyColumn.setNewColumnDefinition(sqlColumnDefinition);
                sqlColumnDefinition.setName(newTableColumn.getName());
                sqlColumnDefinition.setDataType(new SQLDataTypeImpl(newTableColumn.getColumnType()));
                if (BooleanUtils.isNotTrue(newTableColumn.getNullable())) {
                    sqlColumnDefinition.addConstraint(new SQLNotNullConstraint());
                }
                if (!Objects.isNull(newTableColumn.getDefaultValue())) {
                    sqlColumnDefinition.setDefaultExpr(new MySqlCharExpr(newTableColumn.getDefaultValue()));
                }
                sqlColumnDefinition.setAutoIncrement(BooleanUtils.isTrue(newTableColumn.getAutoIncrement()));
                if (!Objects.isNull(newTableColumn.getComment())) {
                    sqlColumnDefinition.setComment(newTableColumn.getComment());
                }
            }
        });

        oldColumnMap.forEach((oldTableColumnName, oldTableColumn) -> {
            TableColumn newTableColumn = newColumnMap.get(oldTableColumnName);
            // 代表删除字段
            if (newTableColumn == null) {
                SQLAlterTableDropColumnItem sqlAlterTableDropColumnItem = new SQLAlterTableDropColumnItem();
                sqlAlterTableStatement.addItem(sqlAlterTableDropColumnItem);
                sqlAlterTableDropColumnItem.addColumn(new SQLIdentifierExpr(oldTableColumn.getName()));
            }
        });

        // 比较主键是否有修改
        // 主键
        Set<String> oldPrimaryKeySet = EasyCollectionUtils.stream(oldTable.getColumnList())
            .filter(tableColumn -> BooleanUtils.isTrue(tableColumn.getPrimaryKey()))
            .map(TableColumn::getName)
            .collect(Collectors.toSet());
        Set<String> newPrimaryKeySet = EasyCollectionUtils.stream(newTable.getColumnList())
            .filter(tableColumn -> BooleanUtils.isTrue(tableColumn.getPrimaryKey()))
            .map(TableColumn::getName)
            .collect(Collectors.toSet());
        boolean primaryKeyChange = oldPrimaryKeySet.stream()
            .anyMatch(oldPrimaryKey -> !newPrimaryKeySet.contains(oldPrimaryKey))
            || newPrimaryKeySet.stream()
            .anyMatch(newPrimaryKey -> !oldPrimaryKeySet.contains(newPrimaryKey));
        if (primaryKeyChange) {
            sqlAlterTableStatement.addItem(new SQLAlterTableDropPrimaryKey());
            SQLAlterTableAddConstraint sqlAlterTableAddConstraint = new SQLAlterTableAddConstraint();
            sqlAlterTableStatement.addItem(sqlAlterTableAddConstraint);
            MySqlPrimaryKey mySqlPrimaryKey = new MySqlPrimaryKey();
            sqlAlterTableAddConstraint.setConstraint(mySqlPrimaryKey);
            mySqlPrimaryKey.setIndexType("PRIMARY");
            // 排序
            EasyCollectionUtils.stream(newTable.getColumnList())
                .filter(tableColumn -> BooleanUtils.isTrue(tableColumn.getPrimaryKey()))
                .map(TableColumn::getName)
                .forEach(tableColumnName -> mySqlPrimaryKey.addColumn(
                    new SQLSelectOrderByItem(new SQLIdentifierExpr(tableColumnName))));
        }

        if (CollectionUtils.isNotEmpty(sqlAlterTableStatement.getItems())) {
            sqlList.add(Sql.builder().sql(sqlAlterTableStatement + ";").build());
        }
    }

    private static void modifyIndex(List<Sql> sqlList, Table oldTable, Table newTable) {
        Map<String, TableIndex> oldIndexMap = EasyCollectionUtils.toIdentityMap(oldTable.getIndexList(),
            TableIndex::getName);
        Map<String, TableIndex> newIndexMap = EasyCollectionUtils.toIdentityMap(newTable.getIndexList(),
            TableIndex::getName);
        newIndexMap.forEach((newTableIndexName, newTableIndex) -> {
            TableIndex oldTableIndex = oldIndexMap.get(newTableIndexName);
            // 代表新增索引
            if (oldTableIndex == null) {
                SQLCreateIndexStatement sqlCreateIndexStatement = new SQLCreateIndexStatement();
                sqlCreateIndexStatement.setTable(new SQLExprTableSource(newTable.getName()));
                sqlCreateIndexStatement.setName(new SQLIdentifierExpr(newTableIndex.getName()));
                if (!Objects.isNull(newTableIndex.getComment())) {
                    sqlCreateIndexStatement.setComment(new SQLCharExpr(newTableIndex.getComment()));
                }
                if (!CollectionUtils.isEmpty(newTableIndex.getColumnList())) {
                    for (TableIndexColumn tableIndexColumn : newTableIndex.getColumnList()) {
                        SQLSelectOrderByItem sqlSelectOrderByItem = new SQLSelectOrderByItem();
                        sqlSelectOrderByItem.setExpr(new SQLIdentifierExpr(tableIndexColumn.getColumnName()));
                        CollationEnum collation = EasyEnumUtils.getEnum(CollationEnum.class,
                            tableIndexColumn.getCollation());
                        if (collation != null) {
                            sqlSelectOrderByItem.setType(collation.getSqlOrderingSpecification());
                        }
                        sqlCreateIndexStatement.getColumns().add(sqlSelectOrderByItem);
                    }
                }
                sqlList.add(Sql.builder().sql(sqlCreateIndexStatement + ";").build());
                return;
            }
            // 代表可能修改索引 或者没变
            boolean hasChange = !StringUtils.equals(oldTableIndex.getName(), newTableIndex.getName())
                || !StringUtils.equals(oldTableIndex.getComment(), newTableIndex.getComment())
                || !Objects.equals(oldTableIndex.getUnique(), newTableIndex.getUnique());
            if (!hasChange) {
                Map<String, TableIndexColumn> oldTableIndexColumnMap = EasyCollectionUtils.toIdentityMap(
                    oldTableIndex.getColumnList(), TableIndexColumn::getColumnName);
                Map<String, TableIndexColumn> newTableIndexColumnMap = EasyCollectionUtils.toIdentityMap(
                    newTableIndex.getColumnList(), TableIndexColumn::getColumnName);
                hasChange = oldTableIndexColumnMap.entrySet()
                    .stream()
                    .anyMatch(oldTableIndexColumnEntry -> {
                        TableIndexColumn newTableIndexColumn = newTableIndexColumnMap.get(
                            oldTableIndexColumnEntry.getKey());
                        if (newTableIndexColumn == null) {
                            return true;
                        }
                        TableIndexColumn oldTableIndexColumn = oldTableIndexColumnEntry.getValue();
                        return !StringUtils.equals(oldTableIndexColumn.getColumnName(),
                            newTableIndexColumn.getColumnName())
                            || !CollationEnum.equals(oldTableIndexColumn.getCollation(),
                            newTableIndexColumn.getCollation());
                    })
                    || newTableIndexColumnMap.entrySet()
                    .stream()
                    .anyMatch(newTableIndexColumnEntry -> {
                        TableIndexColumn oldTableIndexColumn = oldTableIndexColumnMap.get(
                            newTableIndexColumnEntry.getKey());
                        return oldTableIndexColumn == null;
                    });
            }

            // 没有修改索引
            if (!hasChange) {
                return;
            }
            // 先删除
            SQLDropIndexStatement sqlDropIndexStatement = new SQLDropIndexStatement();
            sqlDropIndexStatement.setDbType(DbType.mysql);
            sqlDropIndexStatement.setTableName(new SQLExprTableSource(newTable.getName()));
            sqlDropIndexStatement.setIndexName(new SQLIdentifierExpr(newTableIndex.getName()));
            sqlList.add(Sql.builder().sql(sqlDropIndexStatement + ";").build());

            // 再新增
            SQLCreateIndexStatement sqlCreateIndexStatement = new SQLCreateIndexStatement();
            sqlCreateIndexStatement.setTable(new SQLExprTableSource(newTable.getName()));
            sqlCreateIndexStatement.setName(new SQLIdentifierExpr(newTableIndex.getName()));
            if (!Objects.isNull(newTableIndex.getComment())) {
                sqlCreateIndexStatement.setComment(new SQLCharExpr(newTableIndex.getComment()));
            }
            if (!CollectionUtils.isEmpty(newTableIndex.getColumnList())) {
                for (TableIndexColumn tableIndexColumn : newTableIndex.getColumnList()) {
                    SQLSelectOrderByItem sqlSelectOrderByItem = new SQLSelectOrderByItem();
                    sqlSelectOrderByItem.setExpr(new SQLIdentifierExpr(tableIndexColumn.getColumnName()));
                    CollationEnum collation = EasyEnumUtils.getEnum(CollationEnum.class,
                        tableIndexColumn.getCollation());
                    if (collation != null) {
                        sqlSelectOrderByItem.setType(collation.getSqlOrderingSpecification());
                    }
                    sqlCreateIndexStatement.getColumns().add(sqlSelectOrderByItem);
                }
            }
            sqlList.add(Sql.builder().sql(sqlCreateIndexStatement + ";").build());
        });

        oldIndexMap.forEach((oldTableIndexName, oldTableIndex) -> {
            TableIndex newTableIndex = newIndexMap.get(oldTableIndexName);
            // 代表删除索引
            if (newTableIndex == null) {
                SQLDropIndexStatement sqlDropIndexStatement = new SQLDropIndexStatement();
                sqlDropIndexStatement.setDbType(DbType.mysql);
                sqlDropIndexStatement.setTableName(new SQLExprTableSource(newTable.getName()));
                sqlDropIndexStatement.setIndexName(new SQLIdentifierExpr(oldTableIndex.getName()));
                sqlList.add(Sql.builder().sql(sqlDropIndexStatement + ";").build());
            }
        });
    }

    public static String formatSQLString(Object para) {
        return para != null ? " '" + para + "' " : null;
    }
}