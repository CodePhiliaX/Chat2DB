
package ai.chat2db.server.admin.api.controller.common;

import java.util.List;

import ai.chat2db.server.admin.api.controller.common.converter.CommonAdminConverter;
import ai.chat2db.server.admin.api.controller.common.vo.TeamUserListVO;
import ai.chat2db.server.admin.api.controller.datasource.vo.SimpleDataSourceVO;
import ai.chat2db.server.admin.api.controller.team.vo.SimpleTeamVO;
import ai.chat2db.server.admin.api.controller.user.vo.SimpleUserVO;
import ai.chat2db.server.common.api.controller.request.CommonQueryRequest;
import ai.chat2db.server.domain.api.param.datasource.DataSourceSelector;
import ai.chat2db.server.domain.api.param.team.TeamPageQueryParam;
import ai.chat2db.server.domain.api.param.user.UserPageQueryParam;
import ai.chat2db.server.domain.api.service.DataSourceService;
import ai.chat2db.server.domain.api.service.TeamService;
import ai.chat2db.server.domain.api.service.UserService;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Some general data queries
 *
 * @author Jiaju Zhuang
 */
@RequestMapping("/api/admin/common")
@RestController
public class CommonAdminController {
    private static final DataSourceSelector DATA_SOURCE_SELECTOR = DataSourceSelector.builder()
        .environment(Boolean.TRUE)
        .build();
    @Resource
    private UserService userService;
    @Resource
    private TeamService teamService;
    @Resource
    private DataSourceService dataSourceService;
    @Resource
    private CommonAdminConverter commonAdminConverter;

    /**
     * Fuzzy query of users or teams
     *
     * @param request
     * @return
     * @version 2.1.0
     */
    @GetMapping("/team_user/list")
    public ListResult<TeamUserListVO> teamUserList(@Valid CommonQueryRequest request) {
        UserPageQueryParam userPageQueryParam = commonAdminConverter.request2paramUser(request);
        List<TeamUserListVO> result = Lists.newArrayList();
        result.addAll(userService.pageQuery(userPageQueryParam, null)
            .mapToList(commonAdminConverter::dto2voTeamUser)
            .getData());

        TeamPageQueryParam teamPageQueryParam = commonAdminConverter.request2paramTeam(request);
        result.addAll(teamService.pageQuery(teamPageQueryParam, null)
            .mapToList(commonAdminConverter::dto2voTeamUser)
            .getData());
        return ListResult.of(result);
    }

    /**
     * Fuzzy query of users
     *
     * @param request
     * @return
     * @version 2.1.0
     */
    @GetMapping("/user/list")
    public ListResult<SimpleUserVO> userList(@Valid CommonQueryRequest request) {
        return userService.pageQuery(commonAdminConverter.request2paramUser(request), null)
            .mapToList(commonAdminConverter::dto2voUser);
    }

    /**
     * Fuzzy query of  teams
     *
     * @param request
     * @return
     * @version 2.1.0
     */
    @GetMapping("/team/list")
    public ListResult<SimpleTeamVO> teamList(@Valid CommonQueryRequest request) {
        return teamService.pageQuery(commonAdminConverter.request2paramTeam(request), null)
            .mapToList(commonAdminConverter::dto2voTeam);
    }

    /**
     * Fuzzy query of data source
     *
     * @param request
     * @return
     * @version 2.1.0
     */
    @GetMapping("/data_source/list")
    public ListResult<SimpleDataSourceVO> dataSourceList(@Valid CommonQueryRequest request) {
        return dataSourceService.queryPageWithPermission(commonAdminConverter.request2paramDataSource(request),
                DATA_SOURCE_SELECTOR)
            .mapToList(commonAdminConverter::dto2voDataSource);
    }
}
