package ai.chat2db.server.start.test.core;

import ai.chat2db.server.domain.api.service.ProcedureService;
import ai.chat2db.server.domain.repository.Dbutils;
import ai.chat2db.server.start.test.TestApplication;
import ai.chat2db.server.start.test.dialect.DialectProperties;
import ai.chat2db.server.start.test.dialect.TestUtils;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.tools.common.model.Context;
import ai.chat2db.server.tools.common.model.LoginUser;
import ai.chat2db.server.tools.common.util.ContextUtils;
import ai.chat2db.spi.model.Procedure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Juechen
 * @version : ProcedureServiceTest.java
 */
public class ProcedureServiceTest extends TestApplication {

    @Autowired
    private ProcedureService procedureService;

    @Autowired
    private List<DialectProperties> dialectPropertiesList;

    @Test
    public void testProcedures() {

        userLoginIdentity(false, 2L);

        for (DialectProperties dialectProperties : dialectPropertiesList) {
            Long dataSourceId = TestUtils.nextLong();
            Long consoleId = TestUtils.nextLong();
            String databaseName = "ali_dbhub_test";
//            String databaseName = "Northwind";
//            String databaseName = "618";
            TestUtils.buildContext(dialectProperties, dataSourceId, consoleId, databaseName);

            if (dialectProperties.getDbType().equals("MYSQL")) {
                // No parameters are required here
                ListResult<Procedure> result = procedureService.procedures(databaseName, "");
                System.out.println(result.getData());
                Assertions.assertTrue(result.getSuccess(), result.errorMessage());

                for (Procedure procedure : result.getData()) {
                    String procedureName = procedure.getProcedureName();
                    DataResult<Procedure> detail = procedureService.detail(databaseName, "", procedureName);
                    System.out.println(detail.getData().getProcedureBody());

                    Assertions.assertTrue(detail.getSuccess(), detail.errorMessage());
                }

            } else if (dialectProperties.getDbType().equals("POSTGRESQL")) {
                ListResult<Procedure> result = procedureService.procedures(databaseName, "test");

                System.out.println(result.getData());
                Assertions.assertTrue(result.getSuccess(), result.errorMessage());

                for (Procedure procedure : result.getData()) {
                    String procedureName = procedure.getProcedureName();
                    DataResult<Procedure> detail = procedureService.detail(databaseName, "test", procedureName);
                    System.out.println(detail.getData().getProcedureBody());

                    Assertions.assertTrue(detail.getSuccess(), detail.errorMessage());
                }
            } else if (dialectProperties.getDbType().equals("ORACLE")) {
                ListResult<Procedure> result = procedureService.procedures("", "TEST_USER");

                System.out.println(result.getData());
                Assertions.assertTrue(result.getSuccess(), result.errorMessage());

                for (Procedure procedure : result.getData()) {
                    String procedureName = procedure.getProcedureName();
                    DataResult<Procedure> detail = procedureService.detail("", "TEST_USER", procedureName);
                    System.out.println(detail.getData().getProcedureBody());

                    Assertions.assertTrue(detail.getSuccess(), detail.errorMessage());
                }
            }
        }

    }

    @Test
    public void testUpdate() throws SQLException {

        userLoginIdentity(false, 8L);

        for (DialectProperties dialectProperties : dialectPropertiesList) {
            Long dataSourceId = TestUtils.nextLong();
            Long consoleId = TestUtils.nextLong();
            String databaseName = "ali_dbhub_test";
            TestUtils.buildContext(dialectProperties, dataSourceId, consoleId, databaseName);

            if (dialectProperties.getDbType().equals("MYSQL")) {
                Procedure procedure = new Procedure();
                procedure.setProcedureName("demo_procedure");
                procedure.setProcedureBody("CREATE PROCEDURE demo_procedure(IN param1 VARCHAR(50))\n" +
                        "BEGIN\n" +
                        "END;");
                ActionResult result = procedureService.update(databaseName, "", procedure);
                Assertions.assertTrue(result.getSuccess(), result.errorMessage());
            } else if (dialectProperties.getDbType().equals("ORACLE")) {
                Procedure procedure = new Procedure();
                procedure.setProcedureName("demo_procedure");
                procedure.setProcedureBody("CREATE OR REPLACE PROCEDURE demo_procedure12345 (\n" +
                        "    p_param1 NUMBER\n" +
                        ")\n" +
                        "IS\n" +
                        "BEGIN\n" +
                        "END;\n");
                ActionResult result = procedureService.update("", "TEST_USER", procedure);
                Assertions.assertTrue(result.getSuccess(), result.errorMessage());
            } else if (dialectProperties.getDbType().equals("POSTGRESQL")) {
                Procedure procedure = new Procedure();
                procedure.setProcedureName("demo_procedure");
                procedure.setProcedureBody("CREATE OR REPLACE PROCEDURE demo_procedure(param123 VARCHAR)\n" +
                        "LANGUAGE plpgsql\n" +
                        "AS $$\n" +
                        "BEGIN\n" +
                        "END;\n" +
                        "$$;");
                ActionResult result = procedureService.update(databaseName, "test", procedure);
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
