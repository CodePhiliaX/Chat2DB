package ai.chat2db.server.start.config.interceptor;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

/**
 * Cors跨域的拦截器，任何情况都允许跨域
 *
 * 通过CorsRegistry策略的跨域 在登录的情况下会有有问题，但是本地没有复现，可能原因是：bean 的加载顺序的问题。
 * 临时通过CorsFilter解决，后续可以研究下：CorsRegistry
 *
 * @author 是仪
 */
@Component
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
        throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse)res;
        HttpServletRequest request = (HttpServletRequest)req;

        response.setHeader("Access-Control-Allow-Origin", request.getHeader(HttpHeaders.ORIGIN));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, DBHUB, uid");
        chain.doFilter(req, res);
    }

}
