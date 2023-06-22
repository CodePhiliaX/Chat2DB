package ai.chat2db.server.start.config.oauth;

import cn.dev33.satoken.jwt.StpLogicJwtForStateless;
import cn.dev33.satoken.stp.StpLogic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * satoken配置
 *
 * @author 是仪
 */
@Configuration
public class SaTokenConfigure {
    @Bean
    public StpLogic ttpLogic() {
        // 登录展示用 无状态的 这样不用依赖于与redis之类的
        // 后续可以改成ehcahe 存储磁盘？
        return new StpLogicJwtForStateless();
    }
}