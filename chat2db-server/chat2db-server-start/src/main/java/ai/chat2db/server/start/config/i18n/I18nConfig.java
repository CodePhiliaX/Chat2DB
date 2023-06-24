package ai.chat2db.server.start.config.i18n;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

/**
 * Internationalized configuration
 *
 * @author Jiaju Zhuang
 */
@Configuration
public class I18nConfig {
    @Bean
    public CookieLocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver("CHAT2DB.LOCALE");
        resolver.setDefaultLocale(Locale.US);
        return resolver;
    }
}
