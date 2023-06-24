package ai.chat2db.server.start.exception.convertor;

import java.util.List;

import ai.chat2db.server.tools.base.constant.SymbolConstant;
import ai.chat2db.server.tools.common.util.I18nUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
        msg.append(I18nUtils.getMessage("common.paramCheckError"));
        for (ObjectError e : errors) {
            msg.append(index++);
            // 得到错误消息
            msg.append(SymbolConstant.DOT);
            if (e instanceof FieldError fieldError) {
                msg.append(fieldError.getField());
                msg.append(" : ");
            }
            msg.append(e.getDefaultMessage());
            msg.append(SymbolConstant.SEMICOLON);
        }
        return msg.toString();
    }
}
