package ai.chat2db.server.start.test.core;

import ai.chat2db.server.domain.api.model.Operation;
import ai.chat2db.server.domain.api.param.operation.OperationPageQueryParam;
import ai.chat2db.server.domain.api.param.operation.OperationQueryParam;
import ai.chat2db.server.domain.api.param.operation.OperationSavedParam;
import ai.chat2db.server.domain.api.param.operation.OperationUpdateParam;
import ai.chat2db.server.domain.api.service.OperationService;
import ai.chat2db.server.domain.repository.Dbutils;
import ai.chat2db.server.start.test.TestApplication;
import ai.chat2db.server.start.test.dialect.DialectProperties;
import ai.chat2db.server.start.test.dialect.TestUtils;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import ai.chat2db.server.tools.common.model.Context;
import ai.chat2db.server.tools.common.model.LoginUser;
import ai.chat2db.server.tools.common.util.ContextUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Juechen
 * @version : OperationServiceTest.java
 */
public class OperationServiceTest extends TestApplication {

    @Autowired
    private OperationService operationService;

    @Autowired
    private List<DialectProperties> dialectPropertiesList;


    @Test
    public void testCreateWithPermission() {

        userLoginIdentity(true, 7L);

        for (DialectProperties dialectProperties : dialectPropertiesList) {
            Long dataSourceId = TestUtils.nextLong();
            Long consoleId = TestUtils.nextLong();
            TestUtils.buildContext(dialectProperties, dataSourceId, consoleId);

            OperationSavedParam param = new OperationSavedParam();
            param.setDataSourceId(dataSourceId);
            param.setType(dialectProperties.getDbType());
//            param.setStatus("DRAFT");
            param.setStatus("RELEASE");

            DataResult<Long> result = operationService.createWithPermission(param);
            System.out.println(dialectProperties.getDbType() + "---" + result.getData());
            Assertions.assertTrue(result.success(), result.getErrorMessage());
        }
    }

    @Test
    public void testUpdateWithPermission() {

        userLoginIdentity(true, 3L);

        for (DialectProperties dialectProperties : dialectPropertiesList) {
            Long dataSourceId = TestUtils.nextLong();
            Long consoleId = TestUtils.nextLong();
            TestUtils.buildContext(dialectProperties, dataSourceId, consoleId);

            OperationUpdateParam param = new OperationUpdateParam();
            param.setId(9L);

            ActionResult result = operationService.updateWithPermission(param);
            Assertions.assertTrue(result.success(), result.getErrorMessage());
        }

    }

    @Test
    public void testFind() {
        userLoginIdentity(true, 6L);

        for (DialectProperties dialectProperties : dialectPropertiesList) {
            Long dataSourceId = TestUtils.nextLong();
            Long consoleId = TestUtils.nextLong();
            TestUtils.buildContext(dialectProperties, dataSourceId, consoleId);

            DataResult<Operation> result = operationService.find(18L);
            Assertions.assertTrue(result.success(), result.getErrorMessage());
        }
    }

    @Test
    public void testQueryExistent() {
        userLoginIdentity(false, 7L);

        for (DialectProperties dialectProperties : dialectPropertiesList) {
            Long dataSourceId = TestUtils.nextLong();
            Long consoleId = TestUtils.nextLong();
            TestUtils.buildContext(dialectProperties, dataSourceId, consoleId);

            OperationQueryParam param = new OperationQueryParam();
            param.setId(11L);

            DataResult<Operation> result = operationService.queryExistent(10L);
            DataResult<Operation> result1 = operationService.queryExistent(param);
            Assertions.assertTrue(result.success(), result.getErrorMessage());
            Assertions.assertTrue(result1.success(), result1.getErrorMessage());
        }
    }

    @Test
    public void testDeleteWithPermission() {

        userLoginIdentity(true, 8L);

        for (DialectProperties dialectProperties : dialectPropertiesList) {
            Long dataSourceId = TestUtils.nextLong();
            Long consoleId = TestUtils.nextLong();
            TestUtils.buildContext(dialectProperties, dataSourceId, consoleId);

            OperationSavedParam param = new OperationSavedParam();
            param.setDataSourceId(10L);
            param.setType(dialectProperties.getDbType());
//            param.setStatus("DRAFT");
            param.setStatus("RELEASE");

            DataResult<Long> service = operationService.createWithPermission(param);
            ActionResult result = operationService.deleteWithPermission(service.getData());
            Assertions.assertTrue(result.success(), result.getErrorMessage());

        }
    }

    @Test
    public void testQueryPage() {

        userLoginIdentity(false, 9L);

        for (DialectProperties dialectProperties : dialectPropertiesList) {
            Long dataSourceId = TestUtils.nextLong();
            Long consoleId = TestUtils.nextLong();
            TestUtils.buildContext(dialectProperties, dataSourceId, consoleId);

            OperationPageQueryParam param = new OperationPageQueryParam();
            param.setStatus("RELEASE");
            param.setSearchKey("test");
            param.setDataSourceId(dataSourceId);
            param.setDatabaseName(dialectProperties.getDatabaseName());
            param.setTabOpened("Y");
            param.setPageNo(1);
            param.setPageSize(10);
            param.setOrderByDesc(true);
            param.setOrderByCreateDesc(true);

            PageResult<Operation> result = operationService.queryPage(param);
            Assertions.assertTrue(result.success(), result.getErrorMessage());

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
