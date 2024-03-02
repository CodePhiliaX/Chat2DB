package ai.chat2db.server.web.start.exception.convertor;

import ai.chat2db.server.tools.base.constant.SymbolConstant;
import ai.chat2db.server.tools.common.util.I18nUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;

/**
 * Conversion tool class
 *
 * @author Shi Yi
 */
public class ExceptionConvertorUtils {

    /**
     * Extract error message from BindingResult
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
            // got error message
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
