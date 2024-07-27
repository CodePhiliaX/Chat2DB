package ai.chat2db.server.start.test.core;

import ai.chat2db.server.domain.api.param.PinTableParam;
import ai.chat2db.server.domain.api.service.PinService;
import ai.chat2db.server.domain.repository.Dbutils;
import ai.chat2db.server.start.test.TestApplication;
import ai.chat2db.server.start.test.dialect.DialectProperties;
import ai.chat2db.server.start.test.dialect.TestUtils;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.tools.common.model.Context;
import ai.chat2db.server.tools.common.model.LoginUser;
import ai.chat2db.server.tools.common.util.ContextUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Juechen
 * @version : PinServiceImplTest.java
 */
public class PinServiceTest extends TestApplication {

    @Autowired
    private PinService pinService;

    @Autowired
    private List<DialectProperties> dialectPropertiesList;

    @Test
    public void testPinTable() {

        userLoginIdentity(true, 7L);

        for (DialectProperties dialectProperties : dialectPropertiesList) {
            Long dataSourceId = TestUtils.nextLong();
            Long consoleId = TestUtils.nextLong();
            TestUtils.buildContext(dialectProperties, dataSourceId, consoleId);

            PinTableParam param = new PinTableParam();
            param.setDatabaseName(dialectProperties.getDatabaseName());
            param.setUserId(7L);
            param.setDataSourceId(dataSourceId);
            param.setSchemaName("ali_dbhub_test");
            param.setTableName("t_user");

            ActionResult result = pinService.pinTable(param);
            Assertions.assertTrue(result.success(), result.errorMessage());
        }

    }

    @Test
    public void testDeletePinTable() {

        userLoginIdentity(false,8L);

        for (DialectProperties dialectProperties : dialectPropertiesList) {
            Long dataSourceId = TestUtils.nextLong();
            Long consoleId = TestUtils.nextLong();
            TestUtils.buildContext(dialectProperties, dataSourceId, consoleId);

            PinTableParam param = new PinTableParam();
            param.setDatabaseName(dialectProperties.getDatabaseName());
            param.setUserId(91L);
            param.setDataSourceId(dataSourceId);
            param.setSchemaName("ali_dbhub_test");
            param.setTableName("t_user");

            ActionResult result = pinService.deletePinTable(param);
            Assertions.assertTrue(result.success(), result.errorMessage());
        }
    }

    @Test
    public void testQueryPinTables() {

        userLoginIdentity(false,8L);

        for (DialectProperties dialectProperties : dialectPropertiesList) {
            Long dataSourceId = TestUtils.nextLong();
            Long consoleId = TestUtils.nextLong();
            TestUtils.buildContext(dialectProperties, dataSourceId, consoleId);

            PinTableParam param = new PinTableParam();
            param.setDatabaseName(dialectProperties.getDatabaseName());
            param.setUserId(18L);
            param.setDataSourceId(dataSourceId);
            param.setSchemaName("ali_dbhub_test");
            param.setTableName("t_user");

            ListResult<String> result = pinService.queryPinTables(param);
            Assertions.assertTrue(result.success(), result.errorMessage());
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
