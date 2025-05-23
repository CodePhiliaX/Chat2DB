package ai.chat2db.server.start.test.core;

import ai.chat2db.server.domain.api.param.DropParam;
import ai.chat2db.server.domain.api.param.ShowCreateTableParam;
import ai.chat2db.server.domain.api.param.TableQueryParam;
import ai.chat2db.server.domain.api.param.TableSelector;
import ai.chat2db.server.domain.api.service.TableService;
import ai.chat2db.server.domain.repository.Dbutils;
import ai.chat2db.server.start.test.TestApplication;
import ai.chat2db.server.start.test.dialect.DialectProperties;
import ai.chat2db.server.start.test.dialect.TestUtils;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.common.model.Context;
import ai.chat2db.server.tools.common.model.LoginUser;
import ai.chat2db.server.tools.common.util.ContextUtils;
import ai.chat2db.spi.model.Table;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;

/**
 * @author Juechen
 * @version : TableServiceTest.java
 */
public class TableServiceTest extends TestApplication {

    @Autowired
    private TableService tableService;

    @Autowired
    private List<DialectProperties> dialectPropertiesList;

    @Test
    public void testShowCreateTable() {

        userLoginIdentity(false, 5L);

        for (DialectProperties dialectProperties : dialectPropertiesList) {
            Long dataSourceId = TestUtils.nextLong();
            Long consoleId = TestUtils.nextLong();
            TestUtils.buildContext(dialectProperties, dataSourceId, consoleId);

            if (Objects.equals(dialectProperties.getDbType(), "MYSQL")) {
                ShowCreateTableParam param = new ShowCreateTableParam();
                param.setTableName("chart");
                param.setDatabaseName(dialectProperties.getDatabaseName());
                param.setSchemaName("");

                DataResult<String> result = tableService.showCreateTable(param);
                System.out.println(result.getData());
                Assertions.assertTrue(result.getSuccess(), result.errorMessage());
            } else if (Objects.equals(dialectProperties.getDbType(), "ORACLE")) {
                ShowCreateTableParam param = new ShowCreateTableParam();
                param.setTableName("DEMO");
                param.setDatabaseName("");
                param.setSchemaName("TEST_USER");

                DataResult<String> result = tableService.showCreateTable(param);
                System.out.println(result.getData());
                Assertions.assertTrue(result.getSuccess(), result.errorMessage());
            } else if (Objects.equals(dialectProperties.getDbType(), "MARIADB")) {
                ShowCreateTableParam param = new ShowCreateTableParam();
                param.setTableName("test_data");
                param.setDatabaseName(dialectProperties.getDatabaseName());
                param.setSchemaName("");

                DataResult<String> result = tableService.showCreateTable(param);
                System.out.println(result.getData());
                Assertions.assertTrue(result.getSuccess(), result.errorMessage());
            }
        }

    }

    @Test
    public void testDrop() {
        userLoginIdentity(false, 6L);

        for (DialectProperties dialectProperties : dialectPropertiesList) {
            Long dataSourceId = TestUtils.nextLong();
            Long consoleId = TestUtils.nextLong();
            String databaseName = dialectProperties.getDatabaseName();
            TestUtils.buildContext(dialectProperties, dataSourceId, consoleId, databaseName);

            if (Objects.equals(dialectProperties.getDbType(), "MYSQL")) {
                DropParam param = new DropParam();
                param.setDatabaseName(dialectProperties.getDatabaseName());
                param.setSchema("");
                param.setName("employee_details");

                ActionResult result = tableService.drop(param);
                Assertions.assertTrue(result.success(), result.errorMessage());
            } else if (Objects.equals(dialectProperties.getDbType(), "ORACLE")) {
                DropParam param = new DropParam();
                param.setDatabaseName(dialectProperties.getDatabaseName());
                param.setSchema("TEST_USER");
                param.setName("TEST_USER.DEMO");

                ActionResult result = tableService.drop(param);
                Assertions.assertTrue(result.success(), result.errorMessage());
            } else if (Objects.equals(dialectProperties.getDbType(), "MARIADB")) {
                DropParam param = new DropParam();
                param.setDatabaseName(dialectProperties.getDatabaseName());
                param.setSchema("");
                param.setName("test_data");

                ActionResult result = tableService.drop(param);
                Assertions.assertTrue(result.success(), result.errorMessage());
            } else if (Objects.equals(dialectProperties.getDbType(), "POSTGRESQL")) {
                DropParam param = new DropParam();
                param.setDatabaseName(dialectProperties.getDatabaseName());
                param.setSchema("test");
                param.setName("test.categories_2");

                ActionResult result = tableService.drop(param);
                Assertions.assertTrue(result.success(), result.errorMessage());
            }

        }
    }

    @Test
    public void testQuery() {
        userLoginIdentity(false, 11L);

        for (DialectProperties dialectProperties : dialectPropertiesList) {
            Long dataSourceId = TestUtils.nextLong();
            Long consoleId = TestUtils.nextLong();
            TestUtils.buildContext(dialectProperties, dataSourceId, consoleId);

            if (Objects.equals(dialectProperties.getDbType(), "MYSQL")) {
                TableQueryParam param = new TableQueryParam();
                param.setDatabaseName(dialectProperties.getDatabaseName());
                param.setSchemaName("");
                param.setTableName("access_control_apply_record");
                TableSelector selector = new TableSelector();
                selector.setColumnList(true);
                selector.setIndexList(false);

                DataResult<Table> result = tableService.query(param, selector);
                System.out.println(result.getData());
                Assertions.assertTrue(result.getSuccess(), result.errorMessage());
            } else if (Objects.equals(dialectProperties.getDbType(), "ORACLE")) {
                TableQueryParam param = new TableQueryParam();
                param.setDatabaseName(dialectProperties.getDatabaseName());
                param.setSchemaName("TEST_USER");
                param.setTableName("DEMO_TABLE");
                TableSelector selector = new TableSelector();
                selector.setColumnList(true);
                selector.setIndexList(false);

                DataResult<Table> result = tableService.query(param, selector);
                System.out.println(result.getData());
                Assertions.assertTrue(result.getSuccess(), result.errorMessage());
            } else if (Objects.equals(dialectProperties.getDbType(), "POSTGRESQL")) {
                TableQueryParam param = new TableQueryParam();
                param.setDatabaseName(dialectProperties.getDatabaseName());
                param.setSchemaName("test");
                param.setTableName("dept");
                TableSelector selector = new TableSelector();
                selector.setColumnList(true);
                selector.setIndexList(false);

                DataResult<Table> result = tableService.query(param, selector);
                System.out.println(result.getData());
                Assertions.assertTrue(result.getSuccess(), result.errorMessage());
            }
        }
    }

    /**
     * Save the current user identity (administrator or normal user) and user ID to the context and database session for subsequent use.
     *
     * @param isAdmin
     * @param userId
     */
    private static void userLoginIdentity(boolean isAdmin, Long userId) {
        Context context = Context.builder().loginUser(
                LoginUser.builder().admin(isAdmin).id(userId).build()
        ).build();
        ContextUtils.setContext(context);
        Dbutils.setSession();
    }
}
