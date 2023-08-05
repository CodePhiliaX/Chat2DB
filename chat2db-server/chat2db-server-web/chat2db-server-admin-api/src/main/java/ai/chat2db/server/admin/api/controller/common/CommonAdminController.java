
package ai.chat2db.server.admin.api.controller.common;

import ai.chat2db.server.admin.api.controller.common.request.CommonPageQueryRequest;
import ai.chat2db.server.admin.api.controller.common.vo.TeamUserListVO;
import ai.chat2db.server.admin.api.controller.datasource.converter.DataSourceAdminConverter;
import ai.chat2db.server.domain.api.service.DataSourceService;
import ai.chat2db.server.tools.base.wrapper.result.web.WebPageResult;
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

    @Resource
    private DataSourceService dataSourceService;
    @Resource
    private DataSourceAdminConverter dataSourceAdminConverter;

    /**
     * Fuzzy query of users or teams
     *
     * @param request
     * @return
     * @version 2.1.0
     */
    @GetMapping("/team_user/list")
    public WebPageResult<TeamUserListVO> teamUserList(@Valid CommonPageQueryRequest request) {
        return null;
    }

    /**
     * Fuzzy query of users
     *
     * @param request
     * @return
     * @version 2.1.0
     */
    @GetMapping("/user/list")
    public WebPageResult<TeamUserListVO> userList(@Valid CommonPageQueryRequest request) {
        return null;
    }

    /**
     * Fuzzy query of  teams
     *
     * @param request
     * @return
     * @version 2.1.0
     */
    @GetMapping("/team/list")
    public WebPageResult<TeamUserListVO> teamList(@Valid CommonPageQueryRequest request) {
        return null;
    }

}
