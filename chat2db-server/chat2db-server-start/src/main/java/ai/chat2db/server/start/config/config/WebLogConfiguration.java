package ai.chat2db.server.start.config.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.BodyFilter;

/**
 * log config
 *
 * @author Jiaju Zhuang
 */
@Configuration
public class WebLogConfiguration {

    @Bean
    public BodyFilter bodyFilter() {
        return BodyFilter.none();
    }
}
