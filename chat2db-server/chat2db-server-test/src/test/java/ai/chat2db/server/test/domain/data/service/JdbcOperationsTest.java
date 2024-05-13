package ai.chat2db.server.test.domain.data.service;

import java.util.Date;
import java.util.List;

import ai.chat2db.spi.model.ExecuteResult;
import ai.chat2db.spi.model.Header;
import jakarta.annotation.Resource;

import ai.chat2db.server.domain.api.param.ConsoleConnectParam;
import ai.chat2db.server.domain.api.param.datasource.DataSourcePreConnectParam;
import ai.chat2db.server.domain.api.param.DlExecuteParam;
import ai.chat2db.server.domain.api.service.ConsoleService;
import ai.chat2db.server.domain.api.service.DataSourceService;
import ai.chat2db.server.domain.api.service.DlTemplateService;
import ai.chat2db.server.test.common.BaseTest;
import ai.chat2db.server.test.domain.data.service.dialect.DialectProperties;
import ai.chat2db.server.test.domain.data.utils.TestUtils;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import com.alibaba.fastjson2.JSON;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * query test
 *
 * @author Jiaju Zhuang
 */
@Slf4j
public class JdbcOperationsTest extends BaseTest {
    /**
     * Table Name
     */
    public static final String TABLE_NAME = "DATA_OPS_TEMPLATE_TEST_" + System.currentTimeMillis();
    private final static String STRING = "STR";
    private final static Date DATE = new Date();
    private final static long NUMBER = 1L;

    @Resource
    private DataSourceService dataSourceService;
    @Resource
    private ConsoleService consoleService;

    @Autowired
    private List<DialectProperties> dialectPropertiesList;

    @Resource
    private DlTemplateService dlTemplateService;

    @Test
    @Order(1)
    public void execute() {
        for (DialectProperties dialectProperties : dialectPropertiesList) {
            String dbTypeEnum = dialectProperties.getDbType();
            Long dataSourceId = TestUtils.nextLong();
            Long consoleId = TestUtils.nextLong();

            // Prepare context
            putConnect(dialectProperties.getUrl(), dialectProperties.getUsername(), dialectProperties.getPassword(),
                dialectProperties.getDbType(), dialectProperties.getDatabaseName(), dataSourceId, consoleId);

            DataSourcePreConnectParam dataSourceCreateParam = new DataSourcePreConnectParam();
            dataSourceCreateParam.setType(dbTypeEnum);
            dataSourceCreateParam.setUrl(dialectProperties.getUrl());
            dataSourceCreateParam.setUser(dialectProperties.getUsername());
            dataSourceCreateParam.setPassword(dialectProperties.getPassword());
            dataSourceService.preConnect(dataSourceCreateParam);

            // Create a console
            ConsoleConnectParam consoleCreateParam = new ConsoleConnectParam();
            consoleCreateParam.setDataSourceId(dataSourceId);
            consoleCreateParam.setConsoleId(consoleId);
            consoleCreateParam.setDatabaseName(dialectProperties.getDatabaseName());
            consoleService.createConsole(consoleCreateParam);

            DlExecuteParam templateQueryParam = new DlExecuteParam();
            templateQueryParam.setConsoleId(consoleId);
            templateQueryParam.setDataSourceId(dataSourceId);
            templateQueryParam.setSql(dialectProperties.getCrateTableSql(TABLE_NAME));
            dlTemplateService.execute(templateQueryParam);

            // insert
            templateQueryParam = new DlExecuteParam();
            templateQueryParam.setConsoleId(consoleId);
            templateQueryParam.setDataSourceId(dataSourceId);
            templateQueryParam.setSql(dialectProperties.getInsertSql(TABLE_NAME, DATE, NUMBER, STRING));
            ListResult<ExecuteResult> executeResult = dlTemplateService.execute(templateQueryParam);
            Assertions.assertTrue(executeResult.getSuccess(), "Query data failed");
            // Assertions.assertEquals(1, listResult.getUpdateCount(), "Query data failed");

            // query
            templateQueryParam = new DlExecuteParam();
            templateQueryParam.setConsoleId(consoleId);
            templateQueryParam.setDataSourceId(dataSourceId);
            templateQueryParam.setSql(dialectProperties.getSelectSqlById(TABLE_NAME, 1L));
            executeResult = dlTemplateService.execute(templateQueryParam);
            log.info("Return data:{}", JSON.toJSONString(executeResult));
            Assertions.assertTrue(executeResult.getSuccess(), "Query data failed");
            List<Header> headerList = executeResult.getData().get(0).getHeaderList();
            Assertions.assertEquals(4L, headerList.size(), "Query data failed");
            Assertions.assertEquals(dialectProperties.toCase("ID"), headerList.get(0).getName(), "Query data failed");

            List<List<String>> dataList = executeResult.getData().get(0).getDataList();
            Assertions.assertEquals(1L, dataList.size(), "Query data failed");
            List<String> data1 = dataList.get(0);
            Assertions.assertEquals(Long.toString(NUMBER), data1.get(0), "Query data failed");
            log.info("date:{},{}", DATE, data1.get(1));
            Assertions.assertEquals(DateUtil.format(DATE, DatePattern.NORM_DATETIME_FORMAT), data1.get(1),
                "Query data failed");
            Assertions.assertEquals(Long.toString(NUMBER), data1.get(2), "Query data failed");
            Assertions.assertEquals(STRING, data1.get(3), "Query data failed");

            // Exception sql
            templateQueryParam = new DlExecuteParam();
            templateQueryParam.setConsoleId(consoleId);
            templateQueryParam.setDataSourceId(dataSourceId);
            templateQueryParam.setSql(dialectProperties.getTableNotFoundSqlById(TABLE_NAME));
            executeResult = dlTemplateService.execute(templateQueryParam);
            log.info("Abnormal sql execution result: {}", JSON.toJSONString(executeResult));
            Assertions.assertFalse(executeResult.getSuccess(), "Exception sql error");
            Assertions.assertNotNull(executeResult.getErrorMessage(), "Exception sql error");

            removeConnect();
        }
    }

    @Test
    @Order(Integer.MAX_VALUE)
    public void dropTable() {
        for (DialectProperties dialectProperties : dialectPropertiesList) {
            try {
                String dbTypeEnum = dialectProperties.getDbType();
                Long dataSourceId = TestUtils.nextLong();
                Long consoleId = TestUtils.nextLong();

                DataSourcePreConnectParam dataSourceCreateParam = new DataSourcePreConnectParam();
                dataSourceCreateParam.setType(dbTypeEnum);
                dataSourceCreateParam.setUrl(dialectProperties.getUrl());
                dataSourceCreateParam.setUser(dialectProperties.getUsername());
                dataSourceCreateParam.setPassword(dialectProperties.getPassword());
                dataSourceService.preConnect(dataSourceCreateParam);

                // Create a console
                ConsoleConnectParam consoleCreateParam = new ConsoleConnectParam();
                consoleCreateParam.setDataSourceId(dataSourceId);
                consoleCreateParam.setConsoleId(consoleId);
                consoleCreateParam.setDatabaseName(dialectProperties.getDatabaseName());
                consoleService.createConsole(consoleCreateParam);

                // Create table structure
                DlExecuteParam templateQueryParam = new DlExecuteParam();
                templateQueryParam.setConsoleId(consoleId);
                templateQueryParam.setDataSourceId(dataSourceId);
                templateQueryParam.setSql(dialectProperties.getDropTableSql(TABLE_NAME));
                dlTemplateService.execute(templateQueryParam);
            } catch (Exception e) {
                log.warn("Failed to delete table structure.", e);
            }
        }
    }

}
