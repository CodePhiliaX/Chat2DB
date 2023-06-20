/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alibaba.dbhub.server.domain.support.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.alibaba.dbhub.server.domain.support.model.Procedure;
import com.alibaba.dbhub.server.domain.support.model.Table;
import com.alibaba.dbhub.server.domain.support.model.TableColumn;
import com.alibaba.dbhub.server.domain.support.model.TableIndexColumn;

/**
 * @author jipengfei
 * @version : ResultSetUtils.java
 */
public class ResultSetUtils {

    public static com.alibaba.dbhub.server.domain.support.model.Function buildFunction(ResultSet resultSet) {
        com.alibaba.dbhub.server.domain.support.model.Function function
            = new com.alibaba.dbhub.server.domain.support.model.Function();
        try {
            function.setDatabaseName(resultSet.getString("FUNCTION_CAT"));
            function.setSchemaName(resultSet.getString("FUNCTION_SCHEM"));
            function.setFunctionName(resultSet.getString("FUNCTION_NAME"));
            function.setRemarks(resultSet.getString("REMARKS"));
            function.setFunctionType(resultSet.getShort("FUNCTION_TYPE"));
            function.setSpecificName(resultSet.getString("SPECIFIC_NAME"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return function;
    }

    public static Procedure buildProcedure(ResultSet resultSet) {
        Procedure procedure = new Procedure();
        try {
            procedure.setDatabaseName(resultSet.getString("PROCEDURE_CAT"));
            procedure.setSchemaName(resultSet.getString("PROCEDURE_SCHEM"));
            procedure.setProcedureName(resultSet.getString("PROCEDURE_NAME"));
            procedure.setRemarks(resultSet.getString("REMARKS"));
            procedure.setProcedureType(resultSet.getShort("PROCEDURE_TYPE"));
            procedure.setSpecificName(resultSet.getString("SPECIFIC_NAME"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return procedure;
    }

    public static TableIndexColumn buildTableIndexColumn(ResultSet resultSet) throws SQLException {
        TableIndexColumn tableIndexColumn = new TableIndexColumn();
        tableIndexColumn.setColumnName(resultSet.getString("COLUMN_NAME"));
        tableIndexColumn.setIndexName(resultSet.getString("INDEX_NAME"));
        tableIndexColumn.setAscOrDesc(resultSet.getString("ASC_OR_DESC"));
        tableIndexColumn.setCardinality(resultSet.getLong("CARDINALITY"));
        tableIndexColumn.setPages(resultSet.getLong("PAGES"));
        tableIndexColumn.setFilterCondition(resultSet.getString("FILTER_CONDITION"));
        tableIndexColumn.setIndexQualifier(resultSet.getString("INDEX_QUALIFIER"));
        // tableIndexColumn.setIndexType(resultSet.getShort("TYPE"));
        tableIndexColumn.setNonUnique(resultSet.getBoolean("NON_UNIQUE"));
        tableIndexColumn.setOrdinalPosition(resultSet.getShort("ORDINAL_POSITION"));
        tableIndexColumn.setDatabaseName(resultSet.getString("TABLE_CAT"));
        tableIndexColumn.setSchemaName(resultSet.getString("TABLE_SCHEM"));
        tableIndexColumn.setTableName(resultSet.getString("TABLE_NAME"));
        return tableIndexColumn;
    }

    public static TableColumn buildColumn(ResultSet resultSet) throws SQLException {
        TableColumn tableColumn = new TableColumn();
        tableColumn.setDatabaseName(resultSet.getString("TABLE_CAT"));
        tableColumn.setSchemaName(resultSet.getString("TABLE_SCHEM"));
        tableColumn.setTableName(resultSet.getString("TABLE_NAME"));
        tableColumn.setName(resultSet.getString("COLUMN_NAME"));
        tableColumn.setComment(resultSet.getString("REMARKS"));
        tableColumn.setDefaultValue(resultSet.getString("COLUMN_DEF"));
        tableColumn.setTypeName(resultSet.getString("TYPE_NAME"));
        tableColumn.setColumnSize(resultSet.getInt("COLUMN_SIZE"));
        tableColumn.setDataType(resultSet.getInt("DATA_TYPE"));
        tableColumn.setNullable(resultSet.getInt("NULLABLE") == 1);
        tableColumn.setOrdinalPosition(resultSet.getInt("ORDINAL_POSITION"));
        tableColumn.setAutoIncrement("YES".equals(resultSet.getString("IS_AUTOINCREMENT")));
        //tableColumn.setGeneratedColumn("YES".equals(resultSet.getString("IS_GENERATEDCOLUMN")));
        tableColumn.setOrdinalPosition(resultSet.getInt("ORDINAL_POSITION"));
        tableColumn.setDecimalDigits(resultSet.getInt("DECIMAL_DIGITS"));
        tableColumn.setNumPrecRadix(resultSet.getInt("NUM_PREC_RADIX"));
        tableColumn.setCharOctetLength(resultSet.getInt("CHAR_OCTET_LENGTH"));
        return tableColumn;
    }

    public static Table buildTable(ResultSet resultSet) throws SQLException {
        Table table = new Table();
        table.setName(resultSet.getString("TABLE_NAME"));
        table.setComment(resultSet.getString("REMARKS"));
        table.setDatabaseName(resultSet.getString("TABLE_CAT"));
        table.setSchemaName(resultSet.getString("TABLE_SCHEM"));
        table.setType(resultSet.getString("TABLE_TYPE"));
        return table;
    }
}