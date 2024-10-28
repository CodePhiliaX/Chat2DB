package ai.chat2db.server.start.test.core;

import ai.chat2db.server.domain.api.model.Task;
import ai.chat2db.server.domain.api.param.TaskCreateParam;
import ai.chat2db.server.domain.api.param.TaskPageParam;
import ai.chat2db.server.domain.api.param.TaskUpdateParam;
import ai.chat2db.server.domain.api.service.TaskService;
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
 * @version : TaskServiceTest.java
 */
public class TaskServiceTest extends TestApplication {

    @Autowired
    private TaskService taskService;

    @Autowired
    private List<DialectProperties> dialectPropertiesList;

    @Test
    public void testCreate() {

        userLoginIdentity(true, 9L);
        for (DialectProperties dialectProperties : dialectPropertiesList) {
            Long dataSourceId = TestUtils.nextLong();
            Long consoleId = TestUtils.nextLong();
            TestUtils.buildContext(dialectProperties, dataSourceId, consoleId);

            if (dialectProperties.getDbType().equalsIgnoreCase("MYSQL")) {
                TaskCreateParam param = new TaskCreateParam();
                param.setDataSourceId(dataSourceId);
                param.setDatabaseName(dialectProperties.getDatabaseName());
                param.setSchemaName("");
                param.setTableName("access_token");
                param.setUserId(9L);
                // INIT -> DOWNLOAD_DATA, UPLOAD_TABLE_DATA, DOWNLOAD_TABLE_STRUCTURE, UPLOAD_TABLE_STRUCTURE
                param.setTaskType("DOWNLOAD_DATA");
                param.setTaskName("juechen");

                DataResult<Long> result = taskService.create(param);
                DataResult<Task> taskDataResult = taskService.get(result.getData());
                System.out.println(taskDataResult.getData());
                Assertions.assertTrue(result.success(), result.errorMessage());
            }
        }

    }

    @Test
    public void testPage() {
        userLoginIdentity(true, 12L);

        for (DialectProperties dialectProperties : dialectPropertiesList) {
            Long dataSourceId = TestUtils.nextLong();
            Long consoleId = TestUtils.nextLong();
            TestUtils.buildContext(dialectProperties, dataSourceId, consoleId);

            if (dialectProperties.getDbType().equalsIgnoreCase("MYSQL")) {
                TaskPageParam param = new TaskPageParam();
                param.setPageNo(1);
                param.setPageSize(10);
                param.setUserId(9L);

                PageResult<Task> result = taskService.page(param);
                System.out.println(result.getData());
                Assertions.assertTrue(result.success(), result.errorMessage());
            }
        }
    }

    @Test
    public void testUpdateStatus() {
        userLoginIdentity(true, 5L);

        for (DialectProperties dialectProperties : dialectPropertiesList) {
            Long dataSourceId = TestUtils.nextLong();
            Long consoleId = TestUtils.nextLong();
            TestUtils.buildContext(dialectProperties, dataSourceId, consoleId);

            if (dialectProperties.getDbType().equalsIgnoreCase("MYSQL")) {
                TaskUpdateParam param = new TaskUpdateParam();
                param.setId(9L);
                // DOWNLOAD_DATA, UPLOAD_TABLE_DATA, DOWNLOAD_TABLE_STRUCTURE, UPLOAD_TABLE_STRUCTURE
                param.setTaskStatus("DOWNLOAD_TABLE_STRUCTURE");
                param.setContent(new byte[0]);
                param.setDownloadUrl("success!");

                ActionResult result = taskService.updateStatus(param);
                DataResult<Task> taskDataResult = taskService.get(param.getId());
                System.out.println(taskDataResult.getData());
                Assertions.assertTrue(result.success(), result.errorMessage());
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
