package ai.chat2db.server.start.test.core;

import ai.chat2db.server.domain.api.service.TriggerService;
import ai.chat2db.server.domain.repository.Dbutils;
import ai.chat2db.server.start.test.TestApplication;
import ai.chat2db.server.start.test.dialect.DialectProperties;
import ai.chat2db.server.start.test.dialect.TestUtils;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.tools.common.model.Context;
import ai.chat2db.server.tools.common.model.LoginUser;
import ai.chat2db.server.tools.common.util.ContextUtils;
import ai.chat2db.spi.model.Trigger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Juechen
 * @version : TriggerServiceTest.java
 */
public class TriggerServiceTest extends TestApplication {

    @Autowired
    private TriggerService triggerService;

    @Autowired
    private List<DialectProperties> dialectPropertiesList;

    @Test
    public void testTriggers() {
        userLoginIdentity(false,9L);

        for (DialectProperties dialectProperties : dialectPropertiesList) {
            Long dataSourceId = TestUtils.nextLong();
            Long consoleId = TestUtils.nextLong();
            TestUtils.buildContext(dialectProperties, dataSourceId, consoleId);

            if (dialectProperties.getDbType().equalsIgnoreCase("mysql")) {
                String databaseName = "ali_dbhub_test";
                ListResult<Trigger> triggers = triggerService.triggers(databaseName, "");
                for (Trigger trigger : triggers.getData()) {
                    DataResult<Trigger> detail = triggerService.detail(databaseName, "", trigger.getTriggerName());
                    System.out.println(detail.getData());
                }
                Assertions.assertTrue(triggers.getSuccess(), triggers.getErrorMessage());
            } else if (dialectProperties.getDbType().equalsIgnoreCase("postgresql")) {
                String databaseName = "ali_dbhub_test";
                ListResult<Trigger> triggers = triggerService.triggers(databaseName, "test");
                for (Trigger trigger : triggers.getData()) {
                    DataResult<Trigger> detail = triggerService.detail(databaseName, "test", trigger.getTriggerName());
                    System.out.println(detail.getData());
                }
                Assertions.assertTrue(triggers.getSuccess(), triggers.getErrorMessage());
            } else if (dialectProperties.getDbType().equalsIgnoreCase("oracle")) {
                String schemaName = "TEST_USER";
                ListResult<Trigger> triggers = triggerService.triggers("", schemaName);
                for (Trigger trigger : triggers.getData()) {
                    DataResult<Trigger> detail = triggerService.detail("", schemaName, trigger.getTriggerName());
                    System.out.println(detail.getData());
                }
                Assertions.assertTrue(triggers.getSuccess(), triggers.getErrorMessage());
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
