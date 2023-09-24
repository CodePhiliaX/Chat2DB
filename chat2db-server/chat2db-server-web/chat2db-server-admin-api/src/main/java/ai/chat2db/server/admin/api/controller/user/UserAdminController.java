
package ai.chat2db.server.admin.api.controller.user;

import ai.chat2db.server.admin.api.controller.user.converter.UserAdminConverter;
import ai.chat2db.server.admin.api.controller.user.request.UserCreateRequest;
import ai.chat2db.server.admin.api.controller.user.request.UserUpdateRequest;
import ai.chat2db.server.admin.api.controller.user.vo.UserPageQueryVO;
import ai.chat2db.server.common.api.controller.request.CommonPageQueryRequest;
import ai.chat2db.server.domain.api.param.team.TeamPageQueryParam.OrderCondition;
import ai.chat2db.server.domain.api.param.user.UserPageQueryParam;
import ai.chat2db.server.domain.api.param.user.UserSelector;
import ai.chat2db.server.domain.api.service.UserService;
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

    private static final UserSelector USER_SELECTOR = UserSelector.builder()
        .modifiedUser(Boolean.TRUE)
        .build();

    @Resource
    private UserService userService;
    @Resource
    private UserAdminConverter userAdminConverter;

    /**
     * Pagination query
     *
     * @param request
     * @return
     * @version 2.1.0
     */
    @GetMapping("/page")
    public WebPageResult<UserPageQueryVO> page(@Valid CommonPageQueryRequest request) {
        UserPageQueryParam param = userAdminConverter.request2param(request);
        param.orderBy(OrderCondition.ID_DESC);
        return userService.pageQuery(param, USER_SELECTOR)
            .mapToWeb(userAdminConverter::dto2vo);
    }

    /**
     * create
     *
     * @param request
     * @return
     * @version 2.1.0
     */
    @PostMapping("/create")
    public DataResult<Long> create(@Valid @RequestBody UserCreateRequest request) {
        return userService.create(userAdminConverter.request2param(request));
    }

    /**
     * update
     *
     * @param request
     * @return
     * @version 2.1.0
     */
    @PostMapping("/update")
    public DataResult<Long> update(@RequestBody UserUpdateRequest request) {
        return userService.update(userAdminConverter.request2param(request));
    }

    /**
     * delete
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public DataResult<Boolean> delete(@PathVariable Long id) {
        return userService.delete(id).toBooleaSuccessnDataResult();
    }
}
