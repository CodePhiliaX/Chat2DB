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
 * 数据源测试
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
            // 创建
            DataSourcePreConnectParam dataSourceCreateParam = new DataSourcePreConnectParam();

            dataSourceCreateParam.setType(dbTypeEnum);
            dataSourceCreateParam.setUrl(dialectProperties.getUrl());
            dataSourceCreateParam.setUser(dialectProperties.getUsername());
            dataSourceCreateParam.setPassword(dialectProperties.getPassword());
            ActionResult dataSourceConnect = dataSourceService.preConnect(dataSourceCreateParam);
            Assertions.assertTrue(dataSourceConnect.getSuccess(), "创建数据库连接池失败");
            // Assertions.assertTrue(DataCenterUtils.JDBC_ACCESSOR_MAP.containsKey(dataSourceId), "创建数据库连接池失败");

            // 关闭
            dataSourceService.close(dataSourceId);
            TestUtils.remove();
        }
    }

    @Test
    @Order(2)
    public void test() {
        for (DialectProperties dialectProperties : dialectPropertiesList) {
            String dbTypeEnum = dialectProperties.getDbType();

            // 创建
            DataSourcePreConnectParam dataSourceCreateParam = new DataSourcePreConnectParam();

            dataSourceCreateParam.setType(dbTypeEnum);
            dataSourceCreateParam.setUrl(dialectProperties.getErrorUrl());
            dataSourceCreateParam.setUser(dialectProperties.getUsername());
            dataSourceCreateParam.setPassword(dialectProperties.getPassword());
            ActionResult dataSourceConnect = dataSourceService.preConnect(dataSourceCreateParam);
            log.info("创建数据库返回:{}", JSON.toJSONString(dataSourceConnect));
            Assertions.assertFalse(dataSourceConnect.getSuccess(), "创建数据库失败错误");
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
            // 创建
            DataSourceCreateParam dataSourceCreateParam = new DataSourceCreateParam();
            dataSourceCreateParam.setAlias(dialectProperties.getDbType()+"_unittest_"+dialectProperties.getDbType());
            dataSourceCreateParam.setType(dbTypeEnum);
            dataSourceCreateParam.setUrl(dialectProperties.getUrl());
            dataSourceCreateParam.setUserName(dialectProperties.getUsername());
            dataSourceCreateParam.setPassword(dialectProperties.getPassword());
            DataResult<Long> dataSourceConnect = dataSourceService.createWithPermission(dataSourceCreateParam);
            Assertions.assertTrue(dataSourceConnect.getSuccess(), "创建数据库连接池失败");
            // Assertions.assertTrue(DataCenterUtils.JDBC_ACCESSOR_MAP.containsKey(dataSourceId), "创建数据库连接池失败");
        }
    }

}
