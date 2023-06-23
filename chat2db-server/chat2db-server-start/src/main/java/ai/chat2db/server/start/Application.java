package ai.chat2db.server.start;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Indexed;

/**
 * 启动类
 *
 * @author Jiaju Zhuang
 */
@SpringBootApplication
@ComponentScan(value = {"ai.chat2db.server"})
@MapperScan("ai.chat2db.server.domain.repository.mapper")
@Indexed
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
