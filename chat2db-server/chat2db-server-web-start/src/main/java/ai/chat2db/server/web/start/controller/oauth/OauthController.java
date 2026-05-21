package ai.chat2db.server.web.start.controller.oauth;

import ai.chat2db.server.domain.api.enums.RoleCodeEnum;
import ai.chat2db.server.domain.api.enums.ValidStatusEnum;
import ai.chat2db.server.domain.api.model.User;
import ai.chat2db.server.domain.api.service.LoginAttemptService;
import ai.chat2db.server.domain.api.service.UserService;
import ai.chat2db.server.web.start.controller.oauth.request.LoginRequest;
import ai.chat2db.server.tools.base.excption.BusinessException;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.common.model.LoginUser;
import ai.chat2db.server.tools.common.util.ContextUtils;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaTokenConsts;
import cn.hutool.crypto.digest.DigestUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * 登录授权服务
 *
 * @author Jiaju Zhuang
 */
@RestController
@RequestMapping("/api/oauth")
@Slf4j
public class OauthController {

    @Resource
    private UserService userService;

    @Resource
    private LoginAttemptService loginAttemptService;

    /**
     * 用户名密码登录
     *
     * @param request
     * @return
     */
    @PostMapping("login_a")
    public DataResult login(@Validated @RequestBody LoginRequest request) {

        // 客户端指纹校验
        String clientFingerprint = getClientFingerprint();
        loginAttemptService.validateAttempt(clientFingerprint);
        // 查询用户
        User user = userService.query(request.getUserName());
        this.validateUser(user);

        if (!DigestUtil.bcryptCheck(request.getPassword(), user.getPassword())) {
            loginAttemptService.recordFailedAttempt(clientFingerprint);
            throw new BusinessException("oauth.passwordIncorrect");
        }
        loginAttemptService.clearAttempts(clientFingerprint);
        return DataResult.of(doLogin(user));
    }

    private String getClientFingerprint() {
        // 从Sa-Token上下文中获取请求对象
        SaRequest request = SaHolder.getRequest();

        // 获取客户端IP（考虑代理情况）
        String ip = Objects.requireNonNullElse(request.getHeader("X-Forwarded-For"),
                request.getHeader("X-Real-IP"))
                .split(",")[0].trim();
        // 获取设备指纹特征
        String userAgent = Objects.requireNonNullElse(request.getHeader("User-Agent"), "");
        String acceptLanguage = Objects.requireNonNullElse(request.getHeader("Accept-Language"), "");
        String secChUa = Objects.requireNonNullElse(request.getHeader("Sec-CH-UA"), "");
        // 组合指纹要素（可根据安全需求调整）
        String fingerprintRaw = String.join("|",
                ip,
                DigestUtil.md5Hex(userAgent),
                DigestUtil.sha256Hex(acceptLanguage),
                DigestUtil.sha1Hex(secChUa));
        // 最终指纹生成（双层哈希增加逆向难度）
        return DigestUtil.sha256Hex(DigestUtil.md5Hex(fingerprintRaw));
    }

    private void validateUser(final User user) {
        if (Objects.isNull(user)) {
            throw new BusinessException("oauth.userNameNotExits");
        }
        if (!ValidStatusEnum.VALID.getCode().equals(user.getStatus())) {
            throw new BusinessException("oauth.invalidUserName");
        }
        if (RoleCodeEnum.DESKTOP.getDefaultUserId().equals(user.getId())) {
            throw new BusinessException("oauth.IllegalUserName");
        }
    }

    private Object doLogin(User user) {
        StpUtil.login(user.getId());
        return SaHolder.getStorage().get(SaTokenConsts.JUST_CREATED_NOT_PREFIX);
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
     * 获取当前登录用户信息
     *
     * @return 当前登录用户信息
     */
    @GetMapping("user_a")
    public DataResult<LoginUser> usera() {
        return DataResult.of(getLoginUser());
    }

    private LoginUser getLoginUser() {
        return ContextUtils.queryLoginUser();
    }

}
