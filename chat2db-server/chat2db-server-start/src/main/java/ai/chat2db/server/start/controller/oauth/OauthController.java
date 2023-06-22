package ai.chat2db.server.start.controller.oauth;

import jakarta.annotation.Resource;

import ai.chat2db.server.domain.api.model.User;
import ai.chat2db.server.domain.api.service.UserService;
import ai.chat2db.server.start.controller.oauth.request.LoginRequest;
import ai.chat2db.server.tools.base.excption.BusinessException;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.common.model.LoginUser;
import ai.chat2db.server.tools.common.util.ContextUtils;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaTokenConsts;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录授权服务
 *
 * @author Jiaju Zhuang
 */
@RestController
@RequestMapping("/oauth")
@Slf4j
public class OauthController {

    @Resource
    private UserService userService;

    /**
     * 用户名密码登录
     *
     * @param request
     * @return
     */
    @PostMapping("login_a")
    public DataResult login(@Validated @RequestBody LoginRequest request) {
        //   查询用户
        User user = userService.query(request.getUserName()).getData();
        if (user == null) {
            throw new BusinessException("当前用户不存在。");
        }
        if (!DigestUtil.bcryptCheck(request.getPassword(), user.getPassword())) {
            throw new BusinessException("您输入的密码有误。");
        }
        StpUtil.login(user.getId());
        Object token = SaHolder.getStorage().get(SaTokenConsts.JUST_CREATED_NOT_PREFIX);
        return DataResult.of(token);
    }

    /**
     * 登出
     *
     * @return
     */
    @PostMapping("logout_a")
    public ActionResult logout() {
        StpUtil.logout();
        return ActionResult.isSuccess();
    }

    /**
     * user
     *
     * @return
     */
    @GetMapping("user")
    public DataResult<LoginUser> user() {
        return DataResult.of(ContextUtils.getLoginUser());
    }

    /**
     * user
     *
     * @return
     */
    @GetMapping("user_a")
    public DataResult<LoginUser> usera() {
        return DataResult.of(ContextUtils.queryLoginUser());
    }

}
