package ai.chat2db.server.tools.common.config;

import ai.chat2db.server.tools.common.enums.ModeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author moji
 * @version SystemProperties.java, v 0.1 November 13, 2022 14:28 moji Exp $
 * @date 2022/11/13
 */
@Configuration
@ConfigurationProperties(prefix = "chat2db")
@Data
public class Chat2dbProperties {

    /**
     * version
     */
    private String version;

    /**
     * gateway
     */
    private GatewayProperties gateway;

    /**
     * mode
     */
    private ModeEnum mode;

    @Data
    public static class GatewayProperties {

        private String baseUrl;
        private String modelBaseUrl;

    }
}
