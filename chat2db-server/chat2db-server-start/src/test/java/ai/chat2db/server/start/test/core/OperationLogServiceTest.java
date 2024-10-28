package ai.chat2db.server.start.test.core;

import ai.chat2db.server.domain.api.model.OperationLog;
import ai.chat2db.server.domain.api.param.operation.OperationLogCreateParam;
import ai.chat2db.server.domain.api.param.operation.OperationLogPageQueryParam;
import ai.chat2db.server.domain.api.service.OperationLogService;
import ai.chat2db.server.domain.repository.Dbutils;
import ai.chat2db.server.start.test.TestApplication;
import ai.chat2db.server.start.test.dialect.DialectProperties;
import ai.chat2db.server.start.test.dialect.TestUtils;
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
 * @version : OperationLogServiceTest.java
 */
public class OperationLogServiceTest extends TestApplication {

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private List<DialectProperties> dialectPropertiesList;


    @Test
    public void testCreate() {

        userLoginIdentity(true, 1L);

        for (DialectProperties dialectProperties : dialectPropertiesList) {
            Long dataSourceId = TestUtils.nextLong();
            Long consoleId = TestUtils.nextLong();
            TestUtils.buildContext(dialectProperties, dataSourceId, consoleId);

            OperationLogCreateParam param = new OperationLogCreateParam();
            param.setDataSourceId(dataSourceId);
            param.setType(dialectProperties.getDbType());

            DataResult<Long> result = operationLogService.create(param);
            Assertions.assertTrue(result.success(), result.errorMessage());
        }
    }

    @Test
    public void testQueryPage() {
        userLoginIdentity(false,14L);

        for (DialectProperties dialectProperties : dialectPropertiesList) {
            Long dataSourceId = TestUtils.nextLong();
            Long consoleId = TestUtils.nextLong();
            TestUtils.buildContext(dialectProperties, dataSourceId, consoleId);

            if (dialectProperties.getDbType().equals("MYSQL")) {
                OperationLogPageQueryParam param = new OperationLogPageQueryParam();
                param.setDataSourceId(dataSourceId);
                param.setSearchKey("test");
                param.setUserId(3L);
                param.setDatabaseName(dialectProperties.getDatabaseName());
                param.setSchemaName("");
                param.setPageNo(1);
                param.setPageSize(10);

                PageResult<OperationLog> queryPage = operationLogService.queryPage(param);
                System.out.println(queryPage.getData());
                Assertions.assertTrue(queryPage.success(), queryPage.errorMessage());
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
