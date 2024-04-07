package ai.chat2db.server.start.test;

import ai.chat2db.server.start.Application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Indexed;

/**
 * Startup of the local environment.
 * Mainly to read some local configurations. For example, log output is different from other environments.
 *
 * @author Shi Yi
 */
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@Indexed
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
