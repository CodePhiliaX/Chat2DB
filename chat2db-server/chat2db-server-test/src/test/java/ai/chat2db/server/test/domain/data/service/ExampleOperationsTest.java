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
 * Sample
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
            log.info("Return table creation statement: {}", createTable);
            Assertions.assertNotNull(createTable, "Query sample failed");
            DataResult<String> alterTable = tableService.alterTableExample(dialectProperties.getDbType());
            log.info("Return the statement to create and modify the table: {}", alterTable);
            Assertions.assertNotNull(alterTable, "Query sample failed");
        }
    }

}
