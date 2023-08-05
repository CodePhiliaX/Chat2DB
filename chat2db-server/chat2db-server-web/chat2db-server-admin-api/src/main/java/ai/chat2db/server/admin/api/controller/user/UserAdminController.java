
package ai.chat2db.server.admin.api.controller.user;

import ai.chat2db.server.admin.api.controller.common.request.CommonPageQueryRequest;
import ai.chat2db.server.admin.api.controller.user.converter.UserAdminConverter;
import ai.chat2db.server.admin.api.controller.user.request.UserCreateRequest;
import ai.chat2db.server.admin.api.controller.user.request.UserUpdateRequest;
import ai.chat2db.server.admin.api.controller.user.vo.UserPageQueryVO;
import ai.chat2db.server.domain.api.service.DataSourceService;
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
 * User Management
 *
 * @author Jiaju Zhuang
 */
@RequestMapping("/api/admin/user")
@RestController
public class UserAdminController {

    @Resource
    private DataSourceService dataSourceService;
    @Resource
    private UserAdminConverter dataSourceAdminConverter;

    /**
     * Pagination query
     *
     * @param request
     * @return
     * @version 2.1.0
     */
    @GetMapping("/page")
    public WebPageResult<UserPageQueryVO> page(@Valid CommonPageQueryRequest request) {
        return null;
    }

    /**
     * create
     *
     * @param request
     * @return
     * @version 2.1.0
     */
    @PostMapping("/create")
    public DataResult<Long> create(@RequestBody UserCreateRequest request) {
        return null;

    }

    /**
     * update
     *
     * @param request
     * @return
     * @version 2.1.0
     */
    @PostMapping("/update")
    public ActionResult update(@RequestBody UserUpdateRequest request) {
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
