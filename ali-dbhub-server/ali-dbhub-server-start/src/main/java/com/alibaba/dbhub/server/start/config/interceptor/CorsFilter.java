package com.alibaba.dbhub.server.start.config.interceptor;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
