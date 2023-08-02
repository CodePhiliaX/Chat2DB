
package ai.chat2db.server.web.api.controller.user;

import ai.chat2db.server.domain.api.model.User;
import ai.chat2db.server.domain.api.param.UserQueryParam;
import ai.chat2db.server.domain.api.service.UserService;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import ai.chat2db.server.tools.base.wrapper.result.web.WebPageResult;
import ai.chat2db.server.web.api.controller.user.converter.UserWebConverter;
import ai.chat2db.server.web.api.controller.user.request.UserCreateRequest;
import ai.chat2db.server.web.api.controller.user.request.UserQueryRequest;
import ai.chat2db.server.web.api.controller.user.request.UserUpdateRequest;
import ai.chat2db.server.web.api.controller.user.vo.UserVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jipengfei
 * @version : UserController.java
 */
@RequestMapping("/api/user")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserWebConverter userWebConverter;

    @GetMapping("/{id}")
    public DataResult<UserVO> query(@PathVariable("id") Long id) {
        return DataResult.of(userWebConverter.dto2vo(userService.query(id).getData()));
    }

    @GetMapping("/list")
    public WebPageResult<UserVO> list(UserQueryRequest request) {
        UserQueryParam userQueryParam = new UserQueryParam();
        userQueryParam.setKeyWord(request.getKeyWord());
        userQueryParam.setPageNo(request.getPageNo());
        userQueryParam.setPageSize(request.getPageSize());
        PageResult<User> pageResult = userService.queryPage(userQueryParam);
        return WebPageResult.of(userWebConverter.dto2vo(pageResult.getData()), pageResult.getTotal(), request);
    }

    /**
     * 新增Key
     *
     * @param request
     * @return
     */
    @PostMapping("/create")
    public DataResult<Long> create(@RequestBody UserCreateRequest request) {
        return userService.create(userWebConverter.createRequest2dto(request));
    }

    /**
     * 更新我的保存
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/update",method = {RequestMethod.POST, RequestMethod.PUT})
    public ActionResult update(@RequestBody UserUpdateRequest request) {
        DataResult<Boolean> result = userService.update(userWebConverter.updateRequest2dto(request));
        return ActionResult.isSuccess();
    }

    /**
     * 删除我的保存
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") Long id) {
        userService.delete(id);
        return ActionResult.isSuccess();
    }
}