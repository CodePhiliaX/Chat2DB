package ai.chat2db.server.test.domain.data.service;

import java.util.List;

import jakarta.annotation.Resource;

import ai.chat2db.server.domain.api.service.TableService;
import ai.chat2db.server.test.common.BaseTest;
import ai.chat2db.server.test.domain.data.service.dialect.DialectProperties;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 样例
 *
 * @author Jiaju Zhuang
 */
@Slf4j
public class ExampleOperationsTest extends BaseTest {

    @Resource
    private TableService tableService;
    @Autowired
    private List<DialectProperties> dialectPropertiesList;

    @Test
    @Order(1)
    public void example() {
        for (DialectProperties dialectProperties : dialectPropertiesList) {
            DataResult<String> createTable = tableService.createTableExample(dialectProperties.getDbType());
            log.info("返回建表语句:{}", createTable);
            Assertions.assertNotNull(createTable, "查询样例失败");
            DataResult<String> alterTable = tableService.alterTableExample(dialectProperties.getDbType());
            log.info("返回建修改表语句:{}", alterTable);
            Assertions.assertNotNull(alterTable, "查询样例失败");
        }
    }

}
