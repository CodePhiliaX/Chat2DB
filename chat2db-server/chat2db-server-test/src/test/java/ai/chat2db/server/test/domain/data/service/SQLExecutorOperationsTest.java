package ai.chat2db.server.test.domain.data.service;

import ai.chat2db.server.domain.api.param.datasource.DataSourceCreateParam;
import ai.chat2db.server.domain.api.param.datasource.DataSourcePreConnectParam;
import ai.chat2db.server.domain.api.service.DataSourceService;
import ai.chat2db.server.test.common.BaseTest;
import ai.chat2db.server.test.domain.data.service.dialect.DialectProperties;
import ai.chat2db.server.test.domain.data.utils.TestUtils;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Data source testing
 *
 * @author Jiaju Zhuang
 */
@Slf4j
public class SQLExecutorOperationsTest extends BaseTest {
    @Resource
    private DataSourceService dataSourceService;
    @Autowired
    private List<DialectProperties> dialectPropertiesList;

    @Test
    @Order(1)
    public void createAndClose() {
        for (DialectProperties dialectProperties : dialectPropertiesList) {
            String dbTypeEnum = dialectProperties.getDbType();
            Long dataSourceId = TestUtils.nextLong();
            TestUtils.buildContext(dialectProperties, dataSourceId, null);
            // creat
            DataSourcePreConnectParam dataSourceCreateParam = new DataSourcePreConnectParam();

            dataSourceCreateParam.setType(dbTypeEnum);
            dataSourceCreateParam.setUrl(dialectProperties.getUrl());
            dataSourceCreateParam.setUser(dialectProperties.getUsername());
            dataSourceCreateParam.setPassword(dialectProperties.getPassword());
            ActionResult dataSourceConnect = dataSourceService.preConnect(dataSourceCreateParam);
            Assertions.assertTrue(dataSourceConnect.getSuccess(), "Failed to create database connection pool");
            // Assertions.assertTrue(DataCenterUtils.JDBC_ACCESSOR_MAP.containsKey(dataSourceId), "Failed to create database connection pool");

            // cloes
            dataSourceService.close(dataSourceId);
            TestUtils.remove();
        }
    }

    @Test
    @Order(2)
    public void test() {
        for (DialectProperties dialectProperties : dialectPropertiesList) {
            String dbTypeEnum = dialectProperties.getDbType();

            // creat
            DataSourcePreConnectParam dataSourceCreateParam = new DataSourcePreConnectParam();

            dataSourceCreateParam.setType(dbTypeEnum);
            dataSourceCreateParam.setUrl(dialectProperties.getErrorUrl());
            dataSourceCreateParam.setUser(dialectProperties.getUsername());
            dataSourceCreateParam.setPassword(dialectProperties.getPassword());
            ActionResult dataSourceConnect = dataSourceService.preConnect(dataSourceCreateParam);
            log.info("Create database returns: {}", JSON.toJSONString(dataSourceConnect));
            Assertions.assertFalse(dataSourceConnect.getSuccess(), "Database creation failed error");
        }
    }
    @Test
    @Order(3)
    public void createDataSource(){
        for (DialectProperties dialectProperties : dialectPropertiesList) {
            if(!dialectProperties.getDbType().equals("CLICKHOUSE")){
                continue;
            }
            String dbTypeEnum = dialectProperties.getDbType();
            Long dataSourceId = TestUtils.nextLong();
            TestUtils.buildContext(dialectProperties, dataSourceId, null);
            // creat
            DataSourceCreateParam dataSourceCreateParam = new DataSourceCreateParam();
            dataSourceCreateParam.setAlias(dialectProperties.getDbType()+"_unittest_"+dialectProperties.getDbType());
            dataSourceCreateParam.setType(dbTypeEnum);
            dataSourceCreateParam.setUrl(dialectProperties.getUrl());
            dataSourceCreateParam.setUserName(dialectProperties.getUsername());
            dataSourceCreateParam.setPassword(dialectProperties.getPassword());
            DataResult<Long> dataSourceConnect = dataSourceService.createWithPermission(dataSourceCreateParam);
            Assertions.assertTrue(dataSourceConnect.getSuccess(), "Failed to create database connection pool");
            // Assertions.assertTrue(DataCenterUtils.JDBC_ACCESSOR_MAP.containsKey(dataSourceId), "Failed to create database connection pool");
        }
    }

}
