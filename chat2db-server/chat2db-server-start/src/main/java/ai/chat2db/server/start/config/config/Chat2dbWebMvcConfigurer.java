package ai.chat2db.server.start.config.config;

import java.io.IOException;
import java.util.Enumeration;

import com.alibaba.fastjson2.JSON;

import ai.chat2db.server.domain.api.enums.RoleCodeEnum;
import ai.chat2db.server.domain.api.enums.ValidStatusEnum;
import ai.chat2db.server.domain.api.model.User;
import ai.chat2db.server.domain.api.service.TeamUserService;
import ai.chat2db.server.domain.api.service.UserService;
import ai.chat2db.server.domain.core.cache.CacheKey;
import ai.chat2db.server.domain.core.cache.MemoryCacheManage;
import ai.chat2db.server.tools.base.constant.SymbolConstant;
import ai.chat2db.server.tools.base.excption.BusinessException;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.common.config.Chat2dbProperties;
import ai.chat2db.server.tools.common.enums.ModeEnum;
import ai.chat2db.server.tools.common.exception.PermissionDeniedBusinessException;
import ai.chat2db.server.tools.common.exception.RedirectBusinessException;
import ai.chat2db.server.tools.common.model.Context;
import ai.chat2db.server.tools.common.model.LoginUser;
import ai.chat2db.server.tools.common.util.ContextUtils;
import ai.chat2db.server.tools.common.util.I18nUtils;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * web项目配置
 *
 * @author 是仪
 */
@Configuration
@Slf4j
public class Chat2dbWebMvcConfigurer implements WebMvcConfigurer {

    /**
     * api前缀
     */
    private static final String API_PREFIX = "/api/";

    /**
     * 全局放行的url
     */
    private static final String[] FRONT_PERMIT_ALL = new String[] {"/favicon.ico", "/error", "/static/**",
        "/api/system", "/login", "/api/system/get_latest_version"};

    @Resource
    private UserService userService;
    @Resource
    private TeamUserService teamUserService;
    @Resource
    private Chat2dbProperties chat2dbProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 所有请求尝试加入用户信息
        registry.addInterceptor(new AsyncHandlerInterceptor() {
                @Override
                public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                    @NotNull Object handler) {
                    String userIdString = (String)StpUtil.getLoginIdDefaultNull();
                    Long userId;
                    // 未登录
                    if (!StringUtils.isNumeric(userIdString)) {
                        if (chat2dbProperties.getMode() == ModeEnum.DESKTOP) {
                            userId = RoleCodeEnum.DESKTOP.getDefaultUserId();
                        } else {
                            return true;
                        }
                    } else {
                        userId = Long.parseLong(userIdString);
                    }
                    Long finalUserId = userId;
                    LoginUser loginUser = MemoryCacheManage.computeIfAbsent(CacheKey.getLoginUserKey(userId), () -> {
                        User user = userService.query(finalUserId).getData();
                        if (user == null) {
                            return null;
                        }
                        if (!ValidStatusEnum.VALID.getCode().equals(user.getStatus())) {
                            StpUtil.logout();
                            throw new BusinessException("oauth.invalidUserName");
                        }
                        boolean admin = RoleCodeEnum.ADMIN.getCode().equals(user.getRoleCode());

                        return LoginUser.builder()
                            .id(user.getId())
                            .nickName(user.getNickName())
                            .admin(admin)
                            .roleCode(user.getRoleCode())
                            .build();
                    });

                    if (loginUser == null) {
                        // 代表用户可能被删除了
                        return true;
                    }

                    ContextUtils.setContext(Context.builder()
                        .loginUser(loginUser)
                        .build());
                    return true;
                }

                @Override
                public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                    Exception ex) throws Exception {
                    // 移除登录信息
                    ContextUtils.removeContext();
                }
            })
            .order(1)
            .addPathPatterns("/**")
            .excludePathPatterns(FRONT_PERMIT_ALL);

        // 校验登录信息
        registry.addInterceptor(new AsyncHandlerInterceptor() {
                @Override
                public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                    @NotNull Object handler) throws IOException {
                    Context context = ContextUtils.queryContext();
                    // 校验登录信息
                    if (context == null) {
                        log.info("访问{},{}需要登录", buildHeaderString(request), SaHolder.getRequest().getUrl());

                        String path = SaHolder.getRequest().getRequestPath();
//                        if(path.startsWith("/login")){
//                            return true;
//                        }
                        if (path.startsWith(API_PREFIX)) {
                            response.getWriter().println(JSON.toJSONString(
                                ActionResult.fail("common.needLoggedIn", I18nUtils.getMessage("common.needLoggedIn"),
                                    "")));
                            return false;
                        } else {
                            throw new RedirectBusinessException(
                                "/login?callback=" + SaFoxUtil.joinParam(request.getRequestURI(),
                                    request.getQueryString()));
                        }
                    }
                    return true;
                }
            })
            .order(2)
            .addPathPatterns("/**")
            // 前端需要放行的链接
            .excludePathPatterns(FRONT_PERMIT_ALL)
            // -a结尾的统一放行
            .excludePathPatterns("/**/*-a")
            // _a结尾的统一放行
            .excludePathPatterns("/**/*_a");

        // 校验权限
        registry.addInterceptor(new AsyncHandlerInterceptor() {
                @Override
                public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                    @NotNull Object handler) throws IOException {
                    LoginUser loginUser = ContextUtils.getLoginUser();
                    if (BooleanUtils.isNotTrue(loginUser.getAdmin())) {
                        throw new PermissionDeniedBusinessException();
                    }
                    return true;
                }
            })
            .order(3)
            .addPathPatterns("/api/admin/**")
            .addPathPatterns("/admin/**")
        ;

    }

    private String buildHeaderString(HttpServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headName = headerNames.nextElement();
            stringBuilder.append(headName);
            stringBuilder.append(SymbolConstant.COLON);
            stringBuilder.append(request.getHeader(headName));
            stringBuilder.append(SymbolConstant.COMMA);
        }
        return stringBuilder.toString();
    }
}
