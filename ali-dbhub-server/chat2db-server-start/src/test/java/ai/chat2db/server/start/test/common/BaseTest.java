package ai.chat2db.server.start.test.common;

import ai.chat2db.server.start.Application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 基础测试类
 *
 * @author Jiaju Zhuang
 **/
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public abstract class BaseTest {

}
