package ai.chat2db.server.test.common;

import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.sql.ConnectInfo;
import ai.chat2db.server.start.Application;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 基础测试类
 *
 * @author Jiaju Zhuang
 **/
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public abstract class BaseTest {

    public void putConnect(String url, String username, String password, String dbType, String database,
        Long dataSourceId, Long consoleId) {
        ConnectInfo connectInfo = new ConnectInfo();
        connectInfo.setUser(username);
        connectInfo.setConsoleId(consoleId);
        connectInfo.setDataSourceId(dataSourceId);
        connectInfo.setPassword(password);
        connectInfo.setDbType(dbType);
        connectInfo.setUrl(url);
        connectInfo.setDatabase(database);
        connectInfo.setConsoleOwn(false);
        Chat2DBContext.putContext(connectInfo);
    }

    public void removeConnect() {
        Chat2DBContext.removeContext();
    }
}
