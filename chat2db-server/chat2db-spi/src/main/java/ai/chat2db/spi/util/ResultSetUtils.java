
package ai.chat2db.spi.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import ai.chat2db.spi.model.*;

/**
 * @author jipengfei
 * @version : ResultSetUtils.java
 */
public class ResultSetUtils {

    public static ai.chat2db.spi.model.Function buildFunction(ResultSet resultSet) {
        ai.chat2db.spi.model.Function function
            = new ai.chat2db.spi.model.Function();
        try {
            function.setDatabaseName(getString(resultSet, "FUNCTION_CAT"));
            function.setSchemaName(getString(resultSet, "FUNCTION_SCHEM"));
            function.setFunctionName(getString(resultSet, "FUNCTION_NAME"));
            function.setRemarks(getString(resultSet, "REMARKS"));
            function.setFunctionType(resultSet.getShort("FUNCTION_TYPE"));
            function.setSpecificName(getString(resultSet, "SPECIFIC_NAME"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return function;
    }

    public static Procedure buildProcedure(ResultSet resultSet) {
        Procedure procedure = new Procedure();
        try {
            procedure.setDatabaseName(getString(resultSet, "PROCEDURE_CAT"));
            procedure.setSchemaName(getString(resultSet, "PROCEDURE_SCHEM"));
            procedure.setProcedureName(getString(resultSet, "PROCEDURE_NAME"));
            procedure.setRemarks(getString(resultSet, "REMARKS"));
            procedure.setProcedureType(resultSet.getShort("PROCEDURE_TYPE"));
            procedure.setSpecificName(getString(resultSet, "SPECIFIC_NAME"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return procedure;
    }


    public static TableIndexColumn buildTableIndexColumn(ResultSet resultSet) throws SQLException {
        TableIndexColumn tableIndexColumn = new TableIndexColumn();
        tableIndexColumn.setColumnName(getString(resultSet, "COLUMN_NAME"));
        tableIndexColumn.setIndexName(getString(resultSet, "INDEX_NAME"));
        tableIndexColumn.setAscOrDesc(getString(resultSet, "ASC_OR_DESC"));
        tableIndexColumn.setCardinality(resultSet.getLong("CARDINALITY"));
        tableIndexColumn.setPages(resultSet.getLong("PAGES"));
        tableIndexColumn.setFilterCondition(getString(resultSet, "FILTER_CONDITION"));
        tableIndexColumn.setIndexQualifier(getString(resultSet, "INDEX_QUALIFIER"));
        // tableIndexColumn.setIndexType(resultSet.getShort("TYPE"));
        tableIndexColumn.setNonUnique(resultSet.getBoolean("NON_UNIQUE"));
        tableIndexColumn.setOrdinalPosition(resultSet.getShort("ORDINAL_POSITION"));
        tableIndexColumn.setDatabaseName(getString(resultSet, "TABLE_CAT"));
        tableIndexColumn.setSchemaName(getString(resultSet, "TABLE_SCHEM"));
        tableIndexColumn.setTableName(getString(resultSet, "TABLE_NAME"));
        return tableIndexColumn;
    }

    public static TableColumn buildColumn(ResultSet resultSet) throws SQLException {
        TableColumn tableColumn = new TableColumn();
        tableColumn.setDatabaseName(getString(resultSet, "TABLE_CAT"));
        tableColumn.setSchemaName(getString(resultSet, "TABLE_SCHEM"));
        tableColumn.setTableName(getString(resultSet, "TABLE_NAME"));
        tableColumn.setName(getString(resultSet, "COLUMN_NAME"));
        tableColumn.setComment(getString(resultSet, "REMARKS"));
        tableColumn.setDefaultValue(getString(resultSet, "COLUMN_DEF"));
        tableColumn.setColumnType(getString(resultSet, "TYPE_NAME"));
        tableColumn.setColumnSize(resultSet.getInt("COLUMN_SIZE"));
        tableColumn.setDataType(resultSet.getInt("DATA_TYPE"));
        tableColumn.setNullable(resultSet.getInt("NULLABLE") == 1);
        tableColumn.setOrdinalPosition(resultSet.getInt("ORDINAL_POSITION"));
        //tableColumn.setAutoIncrement("YES".equals(getString,resultSet,"IS_AUTOINCREMENT")));
        //tableColumn.setGeneratedColumn("YES".equals(getString,resultSet,"IS_GENERATEDCOLUMN")));
        tableColumn.setOrdinalPosition(resultSet.getInt("ORDINAL_POSITION"));
        tableColumn.setDecimalDigits(resultSet.getInt("DECIMAL_DIGITS"));
        tableColumn.setNumPrecRadix(resultSet.getInt("NUM_PREC_RADIX"));
        tableColumn.setCharOctetLength(resultSet.getInt("CHAR_OCTET_LENGTH"));
        return tableColumn;
    }

    public static Table buildTable(ResultSet resultSet) throws SQLException {
        Table table = new Table();
        table.setName(getString(resultSet, "TABLE_NAME"));
        table.setComment(getString(resultSet, "REMARKS"));
        table.setDatabaseName(getString(resultSet, "TABLE_CAT"));
        table.setSchemaName(getString(resultSet, "TABLE_SCHEM"));
        table.setType(getString(resultSet, "TABLE_TYPE"));
        return table;
    }

    private static String getString(ResultSet resultSet, String name) {
        if (resultSet == null) {
            return null;
        }
        try {
            return resultSet.getString(name);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getColumnName(ResultSetMetaData resultSetMetaData, int column) throws SQLException {
        String columnLabel = resultSetMetaData.getColumnLabel(column);
        if (columnLabel != null) {
            return columnLabel;
        }
        return resultSetMetaData.getColumnName(column);
    }
}