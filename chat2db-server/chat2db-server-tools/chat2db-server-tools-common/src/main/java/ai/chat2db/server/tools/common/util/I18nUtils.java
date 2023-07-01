package ai.chat2db.server.tools.common.util;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * i18n utility
 *
 * @author Jiaju Zhuang
 */
@Slf4j
@Component
public class I18nUtils implements InitializingBean {
    public static final String DEFAULT_message_Code="";
    @Resource
    private MessageSource messageSource;
    private static MessageSource messageSourceStatic;

    public static String getMessage(String messageCode) {
        return getMessage(messageCode, null);
    }

    public static String getMessage(String messageCode, @Nullable Object[] args) {
        try {
            return messageSourceStatic.getMessage(messageCode, args, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            log.error("no message.", e);
        }
        return messageSourceStatic.getMessage(messageCode, args, LocaleContextHolder.getLocale());
    }

    /**
     * 是否是英文
     *
     * @return
     */
    public static Boolean isEn() {
        return LocaleContextHolder.getLocale().equals(Locale.US);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        messageSourceStatic = messageSource;
    }
}
