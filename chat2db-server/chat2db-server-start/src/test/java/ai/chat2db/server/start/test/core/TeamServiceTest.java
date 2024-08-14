package ai.chat2db.server.start.test.core;

import ai.chat2db.server.domain.api.model.Team;
import ai.chat2db.server.domain.api.param.team.TeamCreateParam;
import ai.chat2db.server.domain.api.param.team.TeamPageQueryParam;
import ai.chat2db.server.domain.api.param.team.TeamSelector;
import ai.chat2db.server.domain.api.param.team.TeamUpdateParam;
import ai.chat2db.server.domain.core.impl.TeamServiceImpl;
import ai.chat2db.server.domain.repository.Dbutils;
import ai.chat2db.server.start.test.TestApplication;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import ai.chat2db.server.tools.common.model.Context;
import ai.chat2db.server.tools.common.model.LoginUser;
import ai.chat2db.server.tools.common.util.ContextUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

/**
 * @author Juechen
 * @version : TeamServiceTest.java
 */
public class TeamServiceTest extends TestApplication {

    @Autowired
    private TeamServiceImpl teamService;

    @Test
    public void testCreate() {
        userLoginIdentity(false,8L);
        TeamCreateParam param = new TeamCreateParam();
        param.setCode("57935");
        param.setStatus("VALID");
        param.setRoleCode("87129");
        param.setName("DEMO3");
        param.setDescription("this is just a test!");

        DataResult<Long> result = teamService.create(param);
        System.out.println("create team_id :" + result.getData());
        Assertions.assertTrue(result.getSuccess(), result.getErrorMessage());

        ArrayList<Long> list = new ArrayList<>();
        list.add(result.getData());
        ListResult<Team> result1 = teamService.listQuery(list);
        System.out.println("current team :" + result1.getData());
        Assertions.assertTrue(result1.getSuccess(), result1.getErrorMessage());

        TeamPageQueryParam pageQueryParam = new TeamPageQueryParam();
        pageQueryParam.setPageNo(1);
        pageQueryParam.setPageSize(10);
        pageQueryParam.setSearchKey("MO");
        pageQueryParam.setEnableReturnCount(true);
        pageQueryParam.setOrderByList(new ArrayList<>());
        TeamSelector selector = new TeamSelector();
        selector.setModifiedUser(false);

        PageResult<Team> result2 = teamService.pageQuery(pageQueryParam, selector);
        for (Team team : result2.getData()) {
            System.out.println("pageList :" + team + "/n");
        }
        Assertions.assertTrue(result2.getSuccess(), result2.getErrorMessage());

        TeamUpdateParam teamUpdateParam = new TeamUpdateParam();
        teamUpdateParam.setId(result.getData());
        teamUpdateParam.setStatus("INVALID");
        teamUpdateParam.setDescription("already update!");
        teamUpdateParam.setName("Juechen");

        DataResult<Long> result3 = teamService.update(teamUpdateParam);
        System.out.println("update team_id :" + result3.getData());
        Assertions.assertTrue(result3.getSuccess(), result3.getErrorMessage());
        ArrayList<Long> list2 = new ArrayList<>();
        list2.add(result.getData());
        ListResult<Team> result4 = teamService.listQuery(list);
        System.out.println("current team :" + result4.getData());
        Assertions.assertTrue(result4.getSuccess(), result4.getErrorMessage());

        ActionResult delete = teamService.delete(result3.getData());
        Assertions.assertTrue(delete.getSuccess(), delete.getErrorMessage());
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
