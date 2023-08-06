
package ai.chat2db.server.admin.api.controller.team;

import ai.chat2db.server.common.api.controller.request.CommonPageQueryRequest;
import ai.chat2db.server.admin.api.controller.team.request.TeamUserBatchCreateRequest;
import ai.chat2db.server.admin.api.controller.team.vo.TeamUserPageQueryVO;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.web.WebPageResult;
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

    /**
     * Pagination query
     *
     * @param request
     * @return
     * @version 2.1.0
     */
    @GetMapping("/page")
    public WebPageResult<TeamUserPageQueryVO> page(@Valid CommonPageQueryRequest request) {
        return null;
    }

    /**
     * create
     *
     * @param request
     * @return
     * @version 2.1.0
     */
    @PostMapping("/batch_create")
    public DataResult<Long> create(@RequestBody TeamUserBatchCreateRequest request) {
        return null;

    }

    /**
     * delete
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable Long id) {
        return null;
    }
}
