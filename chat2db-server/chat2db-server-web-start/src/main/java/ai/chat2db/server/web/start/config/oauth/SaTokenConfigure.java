package ai.chat2db.server.web.start.config.oauth;

import cn.dev33.satoken.jwt.StpLogicJwtForStateless;
import cn.dev33.satoken.stp.StpLogic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * satoken placement
 *
 * @author Shi Yi
 */
@Configuration
public class SaTokenConfigure {
    @Bean
    public StpLogic ttpLogic() {
        // Login display is stateless, so there is no need to rely on redis or the like.
        // Can it be changed to ehcahe storage disk later?
        return new StpLogicJwtForStateless();
    }
}