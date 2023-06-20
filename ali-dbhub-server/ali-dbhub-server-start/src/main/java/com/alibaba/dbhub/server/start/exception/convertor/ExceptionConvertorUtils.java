package com.alibaba.dbhub.server.start.exception.convertor;

import java.util.List;

import com.alibaba.dbhub.server.tools.base.constant.SymbolConstant;

import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

/**
 * 转换工具类
 *
 * @author 是仪
 */
public class ExceptionConvertorUtils {

    /**
     * 提取BindingResult中的错误消息
     *
     * @param result
     * @return
     */
    public static String buildMessage(BindingResult result) {
        List<ObjectError> errors = result.getAllErrors();
        if (CollectionUtils.isEmpty(errors)) {
            return null;
        }

        int index = 1;
        StringBuilder msg = new StringBuilder();
        msg.append("请检查以下信息：");
        for (ObjectError e : errors) {
            msg.append(index++);
            // 得到错误消息
            msg.append(SymbolConstant.DOT);
            msg.append(e.getDefaultMessage());
            msg.append(SymbolConstant.SEMICOLON);
        }
        return msg.toString();
    }
}
