package ai.chat2db.server.start.test.core;

import ai.chat2db.server.domain.api.model.TeamUser;
import ai.chat2db.server.domain.api.param.team.user.TeamUserComprehensivePageQueryParam;
import ai.chat2db.server.domain.api.param.team.user.TeamUserCreatParam;
import ai.chat2db.server.domain.api.param.team.user.TeamUserPageQueryParam;
import ai.chat2db.server.domain.api.param.team.user.TeamUserSelector;
import ai.chat2db.server.domain.api.service.TeamUserService;
import ai.chat2db.server.domain.repository.Dbutils;
import ai.chat2db.server.start.test.TestApplication;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import ai.chat2db.server.tools.common.model.Context;
import ai.chat2db.server.tools.common.model.LoginUser;
import ai.chat2db.server.tools.common.util.ContextUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Juechen
 * @version : TeamUserServiceTest.java
 */
public class TeamUserServiceTest extends TestApplication {

    @Autowired
    private TeamUserService teamUserService;

    @Test
    public void testPageQuery() {
        userLoginIdentity(false,4L);

        TeamUserCreatParam teamUserCreatParam = new TeamUserCreatParam();
        teamUserCreatParam.setTeamId(1L);
        teamUserCreatParam.setUserId(5L);

        DataResult<Long> longDataResult = teamUserService.create(teamUserCreatParam);
        System.out.println("create id :" + longDataResult.getData());
        Assertions.assertTrue(longDataResult.getSuccess(),longDataResult.getErrorMessage());

        TeamUserPageQueryParam param = new TeamUserPageQueryParam();
        param.setTeamId(teamUserCreatParam.getTeamId());
        param.setUserId(teamUserCreatParam.getUserId());
        TeamUserSelector selector = new TeamUserSelector();
        selector.setTeam(true);
        selector.setUser(true);

        PageResult<TeamUser> pageQuery = teamUserService.pageQuery(param, selector);
        System.out.println("value :" + pageQuery.getData());
        Assertions.assertTrue(pageQuery.getSuccess(),pageQuery.getErrorMessage());

        TeamUserComprehensivePageQueryParam pageQueryParam = new TeamUserComprehensivePageQueryParam();
        pageQueryParam.setPageNo(1);
        pageQueryParam.setPageSize(10);
        pageQueryParam.setUserId(teamUserCreatParam.getUserId());
        pageQueryParam.setTeamId(teamUserCreatParam.getTeamId());
        pageQueryParam.setTeamSearchKey("DE");
        PageResult<TeamUser> comprehensivePageQuery = teamUserService.comprehensivePageQuery(pageQueryParam, selector);
        System.out.println("total value :" + comprehensivePageQuery.getData());
        Assertions.assertTrue(comprehensivePageQuery.getSuccess(),comprehensivePageQuery.getErrorMessage());

        ActionResult actionResult = teamUserService.delete(longDataResult.getData());
        Assertions.assertTrue(actionResult.getSuccess(),actionResult.getErrorMessage());

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
