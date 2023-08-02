package ai.chat2db.server.start.config.config;

import ai.chat2db.server.tools.common.config.Chat2dbProperties;
import com.dtflys.forest.Forest;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

/**
 * forest config
 *
 * @author Jiaju Zhuang
 */
@Configuration
public class Chat2dbForestConfiguration implements InitializingBean {

    @Resource
    private Chat2dbProperties chat2dbProperties;
    @Override
    public void afterPropertiesSet() throws Exception {
        Forest.config()
            .setVariableValue("gatewayBaseUrl", chat2dbProperties.getGateway().getBaseUrl());
    }
}
