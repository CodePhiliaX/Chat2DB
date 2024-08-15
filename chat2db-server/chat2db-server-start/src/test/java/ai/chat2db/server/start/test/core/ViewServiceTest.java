package ai.chat2db.server.start.test.core;

import ai.chat2db.server.domain.api.service.ViewService;
import ai.chat2db.server.domain.repository.Dbutils;
import ai.chat2db.server.start.test.TestApplication;
import ai.chat2db.server.start.test.dialect.DialectProperties;
import ai.chat2db.server.start.test.dialect.TestUtils;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.tools.common.model.Context;
import ai.chat2db.server.tools.common.model.LoginUser;
import ai.chat2db.server.tools.common.util.ContextUtils;
import ai.chat2db.spi.model.Table;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Juechen
 * @version : ViewServiceTest.java
 */
public class ViewServiceTest extends TestApplication {

    @Autowired
    private ViewService viewService;

    @Autowired
    private List<DialectProperties> dialectPropertiesList;

    @Test
    public void testViews() {
        userLoginIdentity(false, 9L);

        for (DialectProperties dialectProperties : dialectPropertiesList) {
            Long dataSourceId = TestUtils.nextLong();
            Long consoleId = TestUtils.nextLong();
            TestUtils.buildContext(dialectProperties, dataSourceId, consoleId);

            if (dialectProperties.getDbType().equalsIgnoreCase("mysql")) {
                String databaseName = "ali_dbhub_test";
                ListResult<Table> views = viewService.views(databaseName, null);
                for (Table table : views.getData()) {
                    DataResult<Table> detail = viewService.detail(databaseName, null, table.getName());
                    System.out.println("mysql:" + detail.getData());
                }
                Assertions.assertTrue(views.getSuccess(),views.getErrorMessage());
            } else if (dialectProperties.getDbType().equalsIgnoreCase("postgresql")) {
                String databaseName = "ali_dbhub_test";
                String schemaName = "test";
                ListResult<Table> views = viewService.views(databaseName, schemaName);
                for (Table table : views.getData()) {
                    DataResult<Table> detail = viewService.detail(databaseName, schemaName, table.getName());
                    System.out.println("postgresql:" + detail.getData());
                }
                Assertions.assertTrue(views.getSuccess(),views.getErrorMessage());
            } else if (dialectProperties.getDbType().equalsIgnoreCase("oracle")) {
                String schemaName = "TEST_USER";
                ListResult<Table> views = viewService.views("", schemaName);
                for (Table table : views.getData()) {
                    DataResult<Table> detail = viewService.detail("", schemaName, table.getName());
                    System.out.println("oracle:" + detail.getData());
                }
                Assertions.assertTrue(views.getSuccess(),views.getErrorMessage());
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
