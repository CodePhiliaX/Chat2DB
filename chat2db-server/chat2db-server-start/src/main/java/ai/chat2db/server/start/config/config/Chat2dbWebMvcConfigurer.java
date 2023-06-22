package ai.chat2db.server.start.config.config;

import java.io.IOException;
import java.util.Enumeration;

import com.alibaba.fastjson2.JSON;

import ai.chat2db.server.domain.api.model.User;
import ai.chat2db.server.domain.api.service.UserService;
import ai.chat2db.server.tools.base.constant.SymbolConstant;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
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
        "/api/system"};

    @Resource
    private UserService userService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 所有请求尝试加入用户信息
        registry.addInterceptor(new AsyncHandlerInterceptor() {
                @Override
                public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                    @NotNull Object handler) {
                    String userIdString = (String)StpUtil.getLoginIdDefaultNull();
                    // 未登录
                    if (!StringUtils.isNumeric(userIdString)) {
                        // TODO 这个版本默认放开登录 不管用户是否登录 都算登录，下个版本做权限
                        userIdString = "1";
                        //return true;
                    }
                    // 已经登录 查询用户信息
                    Long userId = Long.parseLong(userIdString);
                    User user = userService.query(userId).getData();
                    if (user == null) {
                        // 代表用户可能被删除了
                        return true;
                    }

                    ContextUtils.setContext(Context.builder()
                        .loginUser(LoginUser.builder()
                            .id(user.getId()).nickName(user.getNickName())
                            .build())
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
                        if (path.startsWith(API_PREFIX)) {
                            response.getWriter().println(JSON.toJSONString(
                                ActionResult.fail("common.needLoggedIn", I18nUtils.getMessage("common.needLoggedIn"))));
                            return false;
                        } else {
                            throw new RedirectBusinessException(
                                "/login-a#/login?callback=" + SaFoxUtil.joinParam(request.getRequestURI(),
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
