package com.alibaba.dbhub.server.test.domain.data.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.alibaba.dbhub.server.domain.api.param.ConsoleConnectParam;
import com.alibaba.dbhub.server.domain.api.param.DataSourcePreConnectParam;
import com.alibaba.dbhub.server.domain.api.param.DlExecuteParam;
import com.alibaba.dbhub.server.domain.api.service.ConsoleService;
import com.alibaba.dbhub.server.domain.api.service.DataSourceService;
import com.alibaba.dbhub.server.domain.api.service.DlTemplateService;
import com.alibaba.dbhub.server.domain.support.enums.DbTypeEnum;
import com.alibaba.dbhub.server.domain.support.model.ExecuteResult;
import com.alibaba.dbhub.server.domain.support.model.Header;
import com.alibaba.dbhub.server.test.common.BaseTest;
import com.alibaba.dbhub.server.test.domain.data.service.dialect.DialectProperties;
import com.alibaba.dbhub.server.test.domain.data.utils.TestUtils;
import com.alibaba.dbhub.server.tools.base.wrapper.result.ListResult;
import com.alibaba.fastjson2.JSON;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 查询测试
 *
 * @author Jiaju Zhuang
 */
@Slf4j
public class JdbcOperationsTest extends BaseTest {
    /**
     * 表名
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
            DbTypeEnum dbTypeEnum = dialectProperties.getDbType();
            Long dataSourceId = TestUtils.nextLong();
            Long consoleId = TestUtils.nextLong();

            // 准备上下文
            putConnect(dialectProperties.getUrl(), dialectProperties.getUsername(), dialectProperties.getPassword(),
                dialectProperties.getDbType(), dialectProperties.getDatabaseName(), dataSourceId, consoleId);

            DataSourcePreConnectParam dataSourceCreateParam = new DataSourcePreConnectParam();
            dataSourceCreateParam.setType(dbTypeEnum.getCode());
            dataSourceCreateParam.setUrl(dialectProperties.getUrl());
            dataSourceCreateParam.setUser(dialectProperties.getUsername());
            dataSourceCreateParam.setPassword(dialectProperties.getPassword());
            dataSourceService.preConnect(dataSourceCreateParam);

            // 创建控制台
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

            // 插入
            templateQueryParam = new DlExecuteParam();
            templateQueryParam.setConsoleId(consoleId);
            templateQueryParam.setDataSourceId(dataSourceId);
            templateQueryParam.setSql(dialectProperties.getInsertSql(TABLE_NAME, DATE, NUMBER, STRING));
            ListResult<ExecuteResult> executeResult = dlTemplateService.execute(templateQueryParam);
            Assertions.assertTrue(executeResult.getSuccess(), "查询数据失败");
            // Assertions.assertEquals(1, listResult.getUpdateCount(), "查询数据失败");

            // 查询
            templateQueryParam = new DlExecuteParam();
            templateQueryParam.setConsoleId(consoleId);
            templateQueryParam.setDataSourceId(dataSourceId);
            templateQueryParam.setSql(dialectProperties.getSelectSqlById(TABLE_NAME, 1L));
            executeResult = dlTemplateService.execute(templateQueryParam);
            log.info("返回数据:{}", JSON.toJSONString(executeResult));
            Assertions.assertTrue(executeResult.getSuccess(), "查询数据失败");
            List<Header> headerList = executeResult.getData().get(0).getHeaderList();
            Assertions.assertEquals(4L, headerList.size(), "查询数据失败");
            Assertions.assertEquals(dialectProperties.toCase("ID"), headerList.get(0).getName(), "查询数据失败");

            List<List<String>> dataList = executeResult.getData().get(0).getDataList();
            Assertions.assertEquals(1L, dataList.size(), "查询数据失败");
            List<String> data1 = dataList.get(0);
            Assertions.assertEquals(Long.toString(NUMBER), data1.get(0), "查询数据失败");
            log.info("date:{},{}", DATE, data1.get(1));
            Assertions.assertEquals(DateUtil.format(DATE, DatePattern.NORM_DATETIME_FORMAT), data1.get(1),
                "查询数据失败");
            Assertions.assertEquals(Long.toString(NUMBER), data1.get(2), "查询数据失败");
            Assertions.assertEquals(STRING, data1.get(3), "查询数据失败");

            // 异常sql
            templateQueryParam = new DlExecuteParam();
            templateQueryParam.setConsoleId(consoleId);
            templateQueryParam.setDataSourceId(dataSourceId);
            templateQueryParam.setSql(dialectProperties.getTableNotFoundSqlById(TABLE_NAME));
            executeResult = dlTemplateService.execute(templateQueryParam);
            log.info("异常sql执行结果:{}", JSON.toJSONString(executeResult));
            Assertions.assertFalse(executeResult.getSuccess(), "异常sql错误");
            Assertions.assertNotNull(executeResult.getErrorMessage(), "异常sql错误");

            removeConnect();
        }
    }

    @Test
    @Order(Integer.MAX_VALUE)
    public void dropTable() {
        for (DialectProperties dialectProperties : dialectPropertiesList) {
            try {
                DbTypeEnum dbTypeEnum = dialectProperties.getDbType();
                Long dataSourceId = TestUtils.nextLong();
                Long consoleId = TestUtils.nextLong();

                DataSourcePreConnectParam dataSourceCreateParam = new DataSourcePreConnectParam();
                dataSourceCreateParam.setType(dbTypeEnum.getCode());
                dataSourceCreateParam.setUrl(dialectProperties.getUrl());
                dataSourceCreateParam.setUser(dialectProperties.getUsername());
                dataSourceCreateParam.setPassword(dialectProperties.getPassword());
                dataSourceService.preConnect(dataSourceCreateParam);

                // 创建控制台
                ConsoleConnectParam consoleCreateParam = new ConsoleConnectParam();
                consoleCreateParam.setDataSourceId(dataSourceId);
                consoleCreateParam.setConsoleId(consoleId);
                consoleCreateParam.setDatabaseName(dialectProperties.getDatabaseName());
                consoleService.createConsole(consoleCreateParam);

                // 创建表结构
                DlExecuteParam templateQueryParam = new DlExecuteParam();
                templateQueryParam.setConsoleId(consoleId);
                templateQueryParam.setDataSourceId(dataSourceId);
                templateQueryParam.setSql(dialectProperties.getDropTableSql(TABLE_NAME));
                dlTemplateService.execute(templateQueryParam);
            } catch (Exception e) {
                log.warn("删除表结构失败.", e);
            }
        }
    }

}
