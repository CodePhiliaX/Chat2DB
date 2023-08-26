
package ai.chat2db.server.admin.api.controller.team;

import ai.chat2db.server.admin.api.controller.team.converter.TeamUserAdminConverter;
import ai.chat2db.server.admin.api.controller.team.request.TeamPageCommonQueryRequest;
import ai.chat2db.server.admin.api.controller.team.request.TeamUserBatchCreateRequest;
import ai.chat2db.server.admin.api.controller.team.vo.TeamUserPageQueryVO;
import ai.chat2db.server.domain.api.param.team.user.TeamUserCreatParam;
import ai.chat2db.server.domain.api.param.team.user.TeamUserPageQueryParam;
import ai.chat2db.server.domain.api.param.team.user.TeamUserSelector;
import ai.chat2db.server.domain.api.service.TeamUserService;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.web.WebPageResult;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Team User Management
 *
 * @author Jiaju Zhuang
 */
@RequestMapping("/api/admin/team/user")
@RestController
public class TeamUserAdminController {
    private static final TeamUserSelector TEAM_USER_SELECTOR = TeamUserSelector.builder()
        .user(Boolean.TRUE)
        .build();

    @Resource
    private TeamUserService teamUserService;
    @Resource
    private TeamUserAdminConverter teamUserAdminConverter;

    /**
     * Pagination query
     *
     * @param request
     * @return
     * @version 2.1.0
     */
    @GetMapping("/page")
    public WebPageResult<TeamUserPageQueryVO> page(@Valid TeamPageCommonQueryRequest request) {
        return teamUserService.comprehensivePageQuery(teamUserAdminConverter.request2param(request), TEAM_USER_SELECTOR)
            .mapToWeb(teamUserAdminConverter::dto2vo);
    }

    /**
     * create
     *
     * @param request
     * @return
     * @version 2.1.0
     */
    @PostMapping("/batch_create")
    public ActionResult create(@Valid @RequestBody TeamUserBatchCreateRequest request) {
        request.getUserIdList()
            .forEach(userId -> {
                TeamUserPageQueryParam teamUserPageQueryParam = new TeamUserPageQueryParam();
                teamUserPageQueryParam.setTeamId(request.getTeamId());
                teamUserPageQueryParam.setUserId(userId);
                teamUserPageQueryParam.queryOne();
                if (teamUserService.pageQuery(teamUserPageQueryParam, null).hasData()) {
                    return;
                }
                teamUserService.create(TeamUserCreatParam.builder()
                    .teamId(request.getTeamId())
                    .userId(userId)
                    .build());
            });
        return ActionResult.isSuccess();
    }

    /**
     * delete
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public DataResult<Boolean> delete(@PathVariable Long id) {
        return teamUserService.delete(id).toBooleaSuccessnDataResult();
    }
}
