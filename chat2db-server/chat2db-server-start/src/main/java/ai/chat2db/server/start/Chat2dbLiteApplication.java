package ai.chat2db.server.start;

import ai.chat2db.server.domain.repository.Dbutils;
import ai.chat2db.server.tools.common.util.ConfigUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Indexed;


/**
 * Chat2DB 简化启动类（无登录鉴权）
 *
 * <p>适用于：
 * <ul>
 *   <li>本地开发测试场景</li>
 *   <li>不需要用户登录鉴权的简化部署</li>
 *   <li>单元测试启动</li>
 * </ul>
 *
 * <p>JAR 文件：chat2db-server-start.jar
 *
 * <p><b>注意：生产环境请使用 Chat2dbWebApplication（chat2db-server-web-start.jar）</b>
 *
 * @author Jiaju Zhuang
 */
@SpringBootApplication(exclude = {
    MailSenderAutoConfiguration.class,
    QuartzAutoConfiguration.class
})
@ComponentScan(value = {"ai.chat2db.server"}, lazyInit = true)
@Indexed
@EnableCaching
@EnableScheduling
@EnableAsync
@Slf4j
public class Chat2dbLiteApplication {

    private static long startTime;

    public static void main(String[] args) {
        startTime = System.currentTimeMillis();
        log.info("[Startup] Starting Chat2dbLiteApplication...");
        ConfigUtils.initProcess();
        new Thread(() -> {
            Dbutils.init();
        }).start();
        SpringApplication.run(Chat2dbLiteApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        long elapsed = System.currentTimeMillis() - startTime;
        log.info("[Startup] Chat2dbLiteApplication started successfully in {}ms ({}s)", elapsed, String.format("%.2f", elapsed / 1000.0));
    }
}
