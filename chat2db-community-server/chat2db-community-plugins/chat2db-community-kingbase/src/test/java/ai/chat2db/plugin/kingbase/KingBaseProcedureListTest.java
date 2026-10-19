package ai.chat2db.plugin.kingbase;

import ai.chat2db.community.domain.api.model.metadata.Procedure;
import org.junit.jupiter.api.Test;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetMetaDataImpl;
import javax.sql.rowset.RowSetProvider;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import static ai.chat2db.plugin.kingbase.constant.SqlConstant.PROCEDURE_LIST_SQL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KingBaseProcedureListTest {

    /**
     * The JDBC driver builds its procedure list from
     * {@code p.proname || '_' || p.oid AS SPECIFIC_NAME}, which KingbaseES rejects with
     * "operator does not exist: boolean || oid". The plugin has to answer the list itself, so
     * {@link java.sql.DatabaseMetaData#getProcedures} must never be reached.
     */
    @Test
    void procedureListIsServedByThePluginSql() throws Exception {
        List<String> prepared = new ArrayList<>();
        List<String> bindings = new ArrayList<>();
        ResultSet result = rows(new String[]{"proname", "nspname", "prokind"},
                new Object[]{"p_sync_data", "tetramdm", "p"},
                new Object[]{"  p_trim_me  ", "tetramdm", "p"},
                new Object[]{null, "tetramdm", "p"});
        Connection connection = proxy(Connection.class, (p, method, args) -> switch (method.getName()) {
            case "prepareStatement" -> {
                prepared.add((String) args[0]);
                yield statement((String) args[0], result, bindings);
            }
            default -> throw new UnsupportedOperationException(method.getName());
        });

        List<Procedure> procedures = new KingBaseMetaData().procedures(connection, "tetramdm_dev", "tetramdm");

        assertEquals(List.of(PROCEDURE_LIST_SQL), prepared);
        assertFalse(PROCEDURE_LIST_SQL.contains("||"), "KingbaseES rejects the driver's || based SPECIFIC_NAME");
        assertTrue(PROCEDURE_LIST_SQL.contains("p.prokind = 'p'"), "V8R6 and later tag procedures with 'p'");
        assertTrue(PROCEDURE_LIST_SQL.contains("p.prorettype = 2278"), "V8R3 tags every user routine 'u'");
        assertEquals(List.of("tetramdm"), bindings);
        assertEquals(2, procedures.size());
        assertEquals("p_sync_data", procedures.get(0).getProcedureName());
        assertEquals("tetramdm_dev", procedures.get(0).getDatabaseName());
        assertEquals("tetramdm", procedures.get(0).getSchemaName());
        assertEquals("p_trim_me", procedures.get(1).getProcedureName());
    }

    private static PreparedStatement statement(String sql, ResultSet result, List<String> bindings) throws SQLException {
        assertEquals(PROCEDURE_LIST_SQL, sql, "Unexpected query: " + sql);
        return proxy(PreparedStatement.class, (p, method, args) -> switch (method.getName()) {
            case "setString" -> {
                assertEquals(bindings.size() + 1, args[0]);
                bindings.add((String) args[1]);
                yield null;
            }
            case "execute" -> true;
            case "getResultSet" -> result;
            case "close" -> null;
            default -> throw new UnsupportedOperationException(method.getName());
        });
    }

    private static CachedRowSet rows(String[] columns, Object[]... values) throws SQLException {
        CachedRowSet rows = RowSetProvider.newFactory().createCachedRowSet();
        RowSetMetaDataImpl metadata = new RowSetMetaDataImpl();
        metadata.setColumnCount(columns.length);
        for (int i = 0; i < columns.length; i++) {
            metadata.setColumnName(i + 1, columns[i]);
            metadata.setColumnLabel(i + 1, columns[i]);
            metadata.setColumnType(i + 1, Types.VARCHAR);
        }
        rows.setMetaData(metadata);
        for (int i = values.length - 1; i >= 0; i--) {
            rows.moveToInsertRow();
            for (int j = 0; j < columns.length; j++) {
                if (values[i][j] == null) {
                    rows.updateNull(j + 1);
                } else {
                    rows.updateObject(j + 1, values[i][j]);
                }
            }
            rows.insertRow();
            rows.moveToCurrentRow();
        }
        rows.beforeFirst();
        return rows;
    }

    private static <T> T proxy(Class<T> type, InvocationHandler handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, handler));
    }
}
