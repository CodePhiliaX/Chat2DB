package com.alibaba.dbhub.server.web.api.aspect;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.dbhub.server.tools.base.excption.BusinessException;
import com.alibaba.dbhub.server.tools.base.excption.CommonErrorEnum;
import com.alibaba.dbhub.server.tools.base.wrapper.Result;
import com.alibaba.fastjson2.JSON;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author moji
 * @version BusinessExceptionHandler.java, v 0.1 2022年10月10日 14:45 moji Exp $
 * @date 2022/10/10
 */
@Component
@Aspect
@Slf4j
public class BusinessExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private HttpServletRequest httpServletRequest;

    private static final String LOCALE_HEADER = "Accept-Language";

    @Around("within(@com.alibaba.dbhub.server.web.api.aspect.BusinessExceptionAspect *)")
    public Object businessExceptionHandler(ProceedingJoinPoint proceedingJoinPoint) {
        try {
            String method = proceedingJoinPoint.getSignature().getDeclaringTypeName() + proceedingJoinPoint
                .getSignature().getName();
            long s1 = System.currentTimeMillis();
            log.info("proceed begin:{} ,param:{}", method, JSON.toJSONString(proceedingJoinPoint.getArgs()));
            Object result = proceedingJoinPoint.proceed();
            long cost = System.currentTimeMillis() - s1;
            log.info("proceed end:{}, result:{}, cost:{}", method, JSON.toJSONString(result), cost);
            return result;
        } catch (Throwable t) {
            log.error("invoke method {} error: {}.", proceedingJoinPoint.getSignature().getName(),
                JSON.toJSONString(proceedingJoinPoint.getArgs()), t);

            if (t instanceof BusinessException) {
                BusinessException exception = (BusinessException)t;
                String message = getMessage(exception.getCode(),  exception.getMessage());
                return error(proceedingJoinPoint, exception.getCode(), message);
            }
            String message = getMessage(CommonErrorEnum.COMMON_SYSTEM_ERROR.name(), t.getMessage());
            return error(proceedingJoinPoint, CommonErrorEnum.COMMON_SYSTEM_ERROR.name(), message);
        }
    }

    private Object error(ProceedingJoinPoint proceedingJoinPoint, String errorCode, String errorMessage) {
        try {
            Signature signature = proceedingJoinPoint.getSignature();
            Class<?> returnType = ((MethodSignature)signature).getReturnType();
            Object resultInstance = returnType.newInstance();
            Result<Object> result = (Result<Object>)resultInstance;
            result.success(false);
            result.errorCode(errorCode);
            result.errorMessage(errorMessage);
            return result;
        } catch (Exception e) {
            log.error("invalid return type!", e);
            Object resultInstance = new Object();
            Result<Object> result = (Result<Object>)resultInstance;
            result.success(false);
            result.errorCode(CommonErrorEnum.COMMON_SYSTEM_ERROR.name());
            result.errorMessage(e.getMessage());
            return result;
        }
    }

    /**
     * get i18n message
     *
     * @param code
     * @param message
     * @return
     */
    private String getMessage(String code, String message) {
        try {
            HttpServletRequest servletRequest
                = ((ServletRequestAttributes)(RequestContextHolder.currentRequestAttributes())).getRequest();
            Locale locale = servletRequest.getHeaders(LOCALE_HEADER).hasMoreElements() ?
                new Locale(servletRequest.getHeaders(LOCALE_HEADER).nextElement()) : Locale.CHINA;
            return messageSource.getMessage(code,
                null, message, locale);
        } catch (Exception exception) {
            log.error("get i18n message error", exception);
        }
        return code;
    }
}
