
package ai.chat2db.server.admin.api.controller.user;

import ai.chat2db.server.admin.api.controller.user.converter.UserTeamAdminConverter;
import ai.chat2db.server.admin.api.controller.user.request.UserPageCommonQueryRequest;
import ai.chat2db.server.admin.api.controller.user.request.UserTeamBatchCreateRequest;
import ai.chat2db.server.admin.api.controller.user.vo.UserTeamPageQueryVO;
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
 * User Team Management
 *
 * @author Jiaju Zhuang
 */
@RequestMapping("/api/admin/user/team")
@RestController
public class UserTeamAdminController {
    private static final TeamUserSelector TEAM_USER_SELECTOR = TeamUserSelector.builder()
        .team(Boolean.TRUE)
        .build();
    @Resource
    private TeamUserService teamUserService;
    @Resource
    private UserTeamAdminConverter userTeamAdminConverter;

    /**
     * Pagination query
     *
     * @param request
     * @return
     * @version 2.1.0
     */
    @GetMapping("/page")
    public WebPageResult<UserTeamPageQueryVO> page(@Valid UserPageCommonQueryRequest request) {
        return teamUserService.comprehensivePageQuery(userTeamAdminConverter.request2param(request), TEAM_USER_SELECTOR)
            .mapToWeb(userTeamAdminConverter::dto2vo);
    }

    /**
     * create
     *
     * @param request
     * @return
     * @version 2.1.0
     */
    @PostMapping("/batch_create")
    public ActionResult bacthCreate(@Valid @RequestBody UserTeamBatchCreateRequest request) {
        request.getTeamIdList()
            .forEach(teamId -> {
                TeamUserPageQueryParam teamUserPageQueryParam = new TeamUserPageQueryParam();
                teamUserPageQueryParam.setTeamId(teamId);
                teamUserPageQueryParam.setUserId(request.getUserId());
                teamUserPageQueryParam.queryOne();
                if (teamUserService.pageQuery(teamUserPageQueryParam, null).hasData()) {
                    return;
                }
                teamUserService.create(TeamUserCreatParam.builder()
                    .teamId(teamId)
                    .userId(request.getUserId())
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
