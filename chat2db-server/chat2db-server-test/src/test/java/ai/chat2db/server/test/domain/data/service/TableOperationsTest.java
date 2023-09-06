package ai.chat2db.server.test.domain.data.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSON;

import ai.chat2db.server.domain.api.param.ConsoleConnectParam;
import ai.chat2db.server.domain.api.param.DlExecuteParam;
import ai.chat2db.server.domain.api.param.DropParam;
import ai.chat2db.server.domain.api.param.ShowCreateTableParam;
import ai.chat2db.server.domain.api.param.TablePageQueryParam;
import ai.chat2db.server.domain.api.param.TableQueryParam;
import ai.chat2db.server.domain.api.param.TableSelector;
import ai.chat2db.server.domain.api.param.datasource.DataSourcePreConnectParam;
import ai.chat2db.server.domain.api.service.ConsoleService;
import ai.chat2db.server.domain.api.service.DataSourceService;
import ai.chat2db.server.domain.api.service.DlTemplateService;
import ai.chat2db.server.domain.api.service.TableService;
import ai.chat2db.server.test.common.BaseTest;
import ai.chat2db.server.test.domain.data.service.dialect.DialectProperties;
import ai.chat2db.server.test.domain.data.utils.TestUtils;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.common.util.EasyCollectionUtils;
import ai.chat2db.spi.enums.CollationEnum;
import ai.chat2db.spi.enums.IndexTypeEnum;
import ai.chat2db.spi.model.Sql;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.model.TableColumn;
import ai.chat2db.spi.model.TableIndex;
import ai.chat2db.spi.model.TableIndexColumn;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 数据源测试
 *
 * @author Jiaju Zhuang
 */
@Slf4j
public class TableOperationsTest extends BaseTest {
    /**
     * 表名
     */
    public static final String TABLE_NAME = "data_ops_table_test_" + System.currentTimeMillis();

    @Resource
    private DataSourceService dataSourceService;
    @Resource
    private ConsoleService consoleService;
    @Autowired
    private List<DialectProperties> dialectPropertiesList;

    @Resource
    private DlTemplateService dlTemplateService;
    @Resource
    private TableService tableService;
    //@Resource
    //private SqlOperations sqlOperations;

    @Test
    @Order(1)
    public void table() {
        for (DialectProperties dialectProperties : dialectPropertiesList) {
            String dbTypeEnum = dialectProperties.getDbType();
            Long dataSourceId = TestUtils.nextLong();
            Long consoleId = TestUtils.nextLong();

            // 准备上下文
            putConnect(dialectProperties.getUrl(), dialectProperties.getUsername(), dialectProperties.getPassword(),
                dialectProperties.getDbType(), dialectProperties.getDatabaseName(), dataSourceId, consoleId);

            DataSourcePreConnectParam dataSourceCreateParam = new DataSourcePreConnectParam();

            dataSourceCreateParam.setType(dbTypeEnum);
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
            templateQueryParam.setSql(dialectProperties.getCrateTableSql(TABLE_NAME));
            dlTemplateService.execute(templateQueryParam);
            // 查询建表语句
            ShowCreateTableParam showCreateTableParam = ShowCreateTableParam.builder()
                .dataSourceId(dataSourceId)
                .databaseName(dialectProperties.getDatabaseName())
                .tableName(dialectProperties.toCase(TABLE_NAME))
                .build();
            if (dialectProperties.getDbType() == "POSTGRESQL") {
                showCreateTableParam.setSchemaName("public");
            }

            DataResult<String> createTable = tableService.showCreateTable(showCreateTableParam);
            log.info("建表语句:{}", createTable.getData());
            if (dialectProperties.getDbType() != "H2") {
                Assertions.assertTrue(createTable.getData().contains(dialectProperties.toCase(TABLE_NAME)),
                    "查询表结构失败");
            }

            //  查询表结构
            TablePageQueryParam tablePageQueryParam = new TablePageQueryParam();
            tablePageQueryParam.setDataSourceId(dataSourceId);
            tablePageQueryParam.setDatabaseName(dialectProperties.getDatabaseName());
            tablePageQueryParam.setTableName(dialectProperties.toCase(TABLE_NAME));
            if (dialectProperties.getDbType() == "POSTGRESQL") {
                tablePageQueryParam.setSchemaName("public");
            }
            List<Table> tableList = tableService.pageQuery(tablePageQueryParam, TableSelector.builder()
                .columnList(Boolean.TRUE)
                .indexList(Boolean.TRUE)
                .build()).getData();
            log.info("分析数据返回{}", JSON.toJSONString(tableList));
            Assertions.assertNotEquals(0L, tableList.size(), "查询表结构失败");
            Table table = tableList.get(0);
            // Assertions.assertEquals(dialectProperties.toCase(TABLE_NAME), table.getName(), "查询表结构失败");
            if (dialectProperties.getDbType() != "POSTGRESQL") {
                Assertions.assertEquals("测试表", table.getComment(), "查询表结构失败");
            }
            TableQueryParam tableQueryParam = new TableQueryParam();
            tableQueryParam.setTableName(table.getName());
            tableQueryParam.setDataSourceId(dataSourceId);
            tableQueryParam.setDatabaseName(dialectProperties.getDatabaseName());
            if (dialectProperties.getDbType() == "POSTGRESQL") {
                tableQueryParam.setSchemaName("public");
            }
            List<TableColumn> columnList = tableService.queryColumns(tableQueryParam);
            Assertions.assertEquals(4L, columnList.size(), "查询表结构失败");
            TableColumn id = columnList.get(0);
            Assertions.assertEquals(dialectProperties.toCase("id"), id.getName(), "查询表结构失败");
            Assertions.assertEquals("主键自增", id.getComment(), "查询表结构失败");
            Assertions.assertTrue(id.getAutoIncrement(), "查询表结构失败");
            //Assertions.assertFalse(id.getNullable(), "查询表结构失败");

            TableColumn string = columnList.get(3);
            Assertions.assertEquals(dialectProperties.toCase("string"), string.getName(), "查询表结构失败");
            //Assertions.assertTrue(string.getNullable(), "查询表结构失败");
            Assertions.assertEquals("DATA", TestUtils.unWrapperDefaultValue(string.getDefaultValue()),
                "查询表结构失败");
            if (dialectProperties.getDbType() == "POSTGRESQL") {
                tablePageQueryParam.setSchemaName("public");
            }
            List<TableIndex> tableIndexList = tableService.queryIndexes(tableQueryParam);
            log.info("分析数据返回{}", JSON.toJSONString(tableIndexList));
            Assertions.assertEquals(4L, tableIndexList.size(), "查询表结构失败");
            Map<String, TableIndex> tableIndexMap = EasyCollectionUtils.toIdentityMap(tableIndexList,
                TableIndex::getName);
            TableIndex idxDate = tableIndexMap.get(dialectProperties.toCase(TABLE_NAME + "_idx_date"));
            Assertions.assertEquals("日期索引", idxDate.getComment(), "查询表结构失败");
            Assertions.assertEquals(IndexTypeEnum.NORMAL.getCode(), idxDate.getType(), "查询表结构失败");
            Assertions.assertEquals(1L, idxDate.getColumnList().size(), "查询表结构失败");
            Assertions.assertEquals(dialectProperties.toCase("date"), idxDate.getColumnList().get(0).getColumnName(),
                "查询表结构失败");
            Assertions.assertEquals(CollationEnum.DESC.getCode(), idxDate.getColumnList().get(0).getCollation(),
                "查询表结构失败");

            TableIndex ukNumber = tableIndexMap.get(dialectProperties.toCase(TABLE_NAME + "_uk_number"));
            Assertions.assertEquals("唯一索引", ukNumber.getComment(), "查询表结构失败");
            Assertions.assertEquals(IndexTypeEnum.UNIQUE.getCode(), ukNumber.getType(), "查询表结构失败");

            TableIndex idxNumberString = tableIndexMap.get(dialectProperties.toCase(TABLE_NAME + "_idx_number_string"));
            Assertions.assertEquals(2, idxNumberString.getColumnList().size(), "查询表结构失败");

            // 删除表结构
            DropParam dropParam = DropParam.builder()
                .dataSourceId(dataSourceId)
                .databaseName(dialectProperties.getDatabaseName())
                .tableName(dialectProperties.toCase(TABLE_NAME))
                .build();
            tableService.drop(dropParam);
            //  查询表结构
            tablePageQueryParam = new TablePageQueryParam();
            tablePageQueryParam.setDataSourceId(dataSourceId);
            tablePageQueryParam.setDatabaseName(dialectProperties.getDatabaseName());
            tablePageQueryParam.setTableName(dialectProperties.toCase(TABLE_NAME));
            tableList = tableService.pageQuery(tablePageQueryParam, TableSelector.builder()
                .columnList(Boolean.TRUE)
                .indexList(Boolean.TRUE)
                .build()).getData();
            log.info("删除表后数据返回{}", JSON.toJSONString(tableList));
            Assertions.assertEquals(0L, tableList.size(), "查询表结构失败");

            // 测试建表语句
            testBuildSql(dialectProperties, dataSourceId, consoleId);

            removeConnect();
        }

    }

    private void testBuildSql(DialectProperties dialectProperties, Long dataSourceId, Long consoleId) {
        if (dialectProperties.getDbType() != "MYSQL") {
            log.error("目前测试案例只支持mysql");
            return;
        }
        // 新建表
        //    CREATE TABLE `DATA_OPS_TEMPLATE_TEST_1673093980449`
        //    (
        //    `id`     bigint PRIMARY KEY AUTO_INCREMENT NOT NULL COMMENT '主键自增',
        //    `date`   datetime(3)                          not null COMMENT '日期',
        //    `number` bigint COMMENT '长整型',
        //    `string` VARCHAR(100) default 'DATA' COMMENT '名字',
        //        index DATA_OPS_TEMPLATE_TEST_1673093980449_idx_date (date desc) comment '日期索引',
        //        unique DATA_OPS_TEMPLATE_TEST_1673093980449_uk_number (number) comment '唯一索引',
        //        index DATA_OPS_TEMPLATE_TEST_1673093980449_idx_number_string (number, date) comment '联合索引'
        //) COMMENT ='测试表';
        //        * 大小写看具体的数据库决定：
        //* 创建表表结构 : 测试表
        //       * 字段：
        //* id   主键自增
        //* date 日期 非空
        //       * number 长整型
        //       * string  字符串 长度100 默认值 "DATA"
        //       *
        //* 索引(加上$tableName_ 原因是 有些数据库索引是全局唯一的)：
        //* $tableName_idx_date 日期索引 倒序
        //       * $tableName_uk_number 唯一索引
        //       * $tableName_idx_number_string 联合索引
        String tableName = dialectProperties.toCase("data_ops_table_test_" + System.currentTimeMillis());
        Table newTable = new Table();
        newTable.setName(tableName);
        newTable.setComment("测试表");
        List<TableColumn> tableColumnList = new ArrayList<>();
        newTable.setColumnList(tableColumnList);
        //id
        TableColumn idTableColumn = new TableColumn();
        idTableColumn.setName("id");
        idTableColumn.setAutoIncrement(Boolean.TRUE);
        idTableColumn.setPrimaryKey(Boolean.TRUE);
        //idTableColumn.setNullable(Boolean.FALSE);
        idTableColumn.setComment("主键自增");
        idTableColumn.setColumnType("bigint");
        tableColumnList.add(idTableColumn);

        // date
        TableColumn dateTableColumn = new TableColumn();
        dateTableColumn.setName("date");
        //dateTableColumn.setNullable(Boolean.FALSE);
        dateTableColumn.setComment("日期");
        dateTableColumn.setColumnType("datetime(3)");
        tableColumnList.add(dateTableColumn);

        // number
        TableColumn numberTableColumn = new TableColumn();
        numberTableColumn.setName("number");
        numberTableColumn.setComment("长整型");
        numberTableColumn.setColumnType("bigint");
        tableColumnList.add(numberTableColumn);

        // string
        TableColumn stringTableColumn = new TableColumn();
        stringTableColumn.setName("string");
        stringTableColumn.setComment("名字");
        stringTableColumn.setColumnType("varchar(100)");
        stringTableColumn.setDefaultValue("DATA");
        tableColumnList.add(stringTableColumn);

        // 索引
        List<TableIndex> tableIndexList = new ArrayList<>();
        newTable.setIndexList(tableIndexList);

        //        index DATA_OPS_TEMPLATE_TEST_1673093980449_idx_date (date desc) comment '日期索引',
        tableIndexList.add(TableIndex.builder()
            .name(tableName + "_idx_date")
            .type(IndexTypeEnum.NORMAL.getCode())
            .comment("日期索引")
            .columnList(Lists.newArrayList(TableIndexColumn.builder()
                .columnName("date")
                .collation(CollationEnum.DESC.getCode())
                .build()))
            .build());

        //        unique DATA_OPS_TEMPLATE_TEST_1673093980449_uk_number (number) comment '唯一索引',
        tableIndexList.add(TableIndex.builder()
            .name(tableName + "_uk_number")
            .type(IndexTypeEnum.UNIQUE.getCode())
            .comment("唯一索引")
            .columnList(Lists.newArrayList(TableIndexColumn.builder()
                .columnName("number")
                .build()))
            .build());
        //        index DATA_OPS_TEMPLATE_TEST_1673093980449_idx_number_string (number, date) comment '联合索引'
        tableIndexList.add(TableIndex.builder()
            .name(tableName + "_idx_number_string")
            .type(IndexTypeEnum.NORMAL.getCode())
            .comment("联合索引")
            .columnList(Lists.newArrayList(TableIndexColumn.builder()
                    .columnName("number")
                    .build(),
                TableIndexColumn.builder()
                    .columnName("date")
                    .build()))
            .build());
        // 构建sql
        List<Sql> buildTableSqlList = tableService.buildSql(null, newTable).getData();
        log.info("创建表的结构语句是:{}", JSON.toJSONString(buildTableSqlList));
        for (Sql sql : buildTableSqlList) {
            DlExecuteParam templateQueryParam = new DlExecuteParam();
            templateQueryParam.setConsoleId(consoleId);
            templateQueryParam.setDataSourceId(dataSourceId);
            templateQueryParam.setSql(sql.getSql());
            dlTemplateService.execute(templateQueryParam);
        }

        // 校验表结构
        checkTable(tableName, dialectProperties, dataSourceId);

        //  去数据库查询表结构
        TableQueryParam tablePageQueryParam = new TableQueryParam();
        tablePageQueryParam.setDataSourceId(dataSourceId);
        tablePageQueryParam.setDatabaseName(dialectProperties.getDatabaseName());
        tablePageQueryParam.setTableName(dialectProperties.toCase(tableName));
        Table table = tableService.query(tablePageQueryParam, TableSelector.builder()
            .columnList(Boolean.TRUE)
            .indexList(Boolean.TRUE)
            .build()).getData();
        log.info("分析数据返回{}", JSON.toJSONString(table));
        Assertions.assertNotNull(table, "查询表结构失败");
        Table oldTable = table;
        Assertions.assertEquals(dialectProperties.toCase(tableName), oldTable.getName(), "查询表结构失败");
        Assertions.assertEquals("测试表", oldTable.getComment(), "查询表结构失败");

        // 修改表结构
        // 构建sql
        log.info("oldTable：{}", JSON.toJSONString(oldTable));
        log.info("newTable：{}", JSON.toJSONString(newTable));
        buildTableSqlList = tableService.buildSql(oldTable, newTable).getData();
        log.info("修改表结构是:{}", JSON.toJSONString(buildTableSqlList));
        Assertions.assertTrue(!buildTableSqlList.isEmpty(), "构建sql失败");
        //  重新去查询下 这样有2个对象
        tablePageQueryParam = new TableQueryParam();
        tablePageQueryParam.setDataSourceId(dataSourceId);
        tablePageQueryParam.setDatabaseName(dialectProperties.getDatabaseName());
        tablePageQueryParam.setTableName(dialectProperties.toCase(tableName));
        newTable = tableService.query(tablePageQueryParam, TableSelector.builder()
            .columnList(Boolean.TRUE)
            .indexList(Boolean.TRUE)
            .build()).getData();

        // 修改字段

        // 新增一个字段
        newTable.getColumnList().add(TableColumn.builder()
            .name("add_string")
            .columnType("varchar(20)")
            .comment("新增的字符串")
            .build());

        // 新增一个索引
        newTable.getIndexList().add(TableIndex.builder()
            .name(tableName + "_idx_string_new")
            .type(IndexTypeEnum.NORMAL.getCode())
            .comment("新的字符串索引")
            .columnList(Lists.newArrayList(TableIndexColumn.builder()
                .columnName("add_string")
                .collation(CollationEnum.DESC.getCode())
                .build()))
            .build());

        // 查询表结构变更
        log.info("oldTable：{}", JSON.toJSONString(oldTable));
        log.info("newTable：{}", JSON.toJSONString(newTable));
        buildTableSqlList = tableService.buildSql(oldTable, newTable).getData();
        log.info("修改表结构是:{}", JSON.toJSONString(buildTableSqlList));

        // 删除表结构
        dropTable(tableName, dialectProperties, dataSourceId);
    }

    private void dropTable(String tableName, DialectProperties dialectProperties, Long dataSourceId) {
        // 删除表结构
        DropParam dropParam = DropParam.builder()
            .dataSourceId(dataSourceId)
            .databaseName(dialectProperties.getDatabaseName())
            .tableName(dialectProperties.toCase(tableName))
            .build();
        tableService.drop(dropParam);
        //  查询表结构
        TablePageQueryParam tablePageQueryParam = new TablePageQueryParam();
        tablePageQueryParam.setDataSourceId(dataSourceId);
        tablePageQueryParam.setDatabaseName(dialectProperties.getDatabaseName());
        tablePageQueryParam.setTableName(dialectProperties.toCase(tableName));
        List<Table> tableList = tableService.pageQuery(tablePageQueryParam, TableSelector.builder()
            .columnList(Boolean.TRUE)
            .indexList(Boolean.TRUE)
            .build()).getData();
        log.info("删除表后数据返回{}", JSON.toJSONString(tableList));
        Assertions.assertEquals(0L, tableList.size(), "查询表结构失败");
    }

    private void checkTable(String tableName, DialectProperties dialectProperties, Long dataSourceId) {
        //  查询表结构
        TablePageQueryParam tablePageQueryParam = new TablePageQueryParam();
        tablePageQueryParam.setDataSourceId(dataSourceId);
        tablePageQueryParam.setDatabaseName(dialectProperties.getDatabaseName());
        tablePageQueryParam.setTableName(dialectProperties.toCase(tableName));
        List<Table> tableList = tableService.pageQuery(tablePageQueryParam, TableSelector.builder()
            .columnList(Boolean.TRUE)
            .indexList(Boolean.TRUE)
            .build()).getData();

        log.info("分析数据返回{}", JSON.toJSONString(tableList));
        Assertions.assertEquals(1L, tableList.size(), "查询表结构失败");
        Table table = tableList.get(0);
        Assertions.assertEquals(dialectProperties.toCase(tableName), table.getName(), "查询表结构失败");
        Assertions.assertEquals("测试表", table.getComment(), "查询表结构失败");
        TableQueryParam tableQueryParam = new TableQueryParam();
        tableQueryParam.setTableName(table.getName());
        tableQueryParam.setDataSourceId(dataSourceId);
        tableQueryParam.setDatabaseName(dialectProperties.getDatabaseName());

        List<TableColumn> columnList = tableService.queryColumns(tableQueryParam);
        Assertions.assertEquals(4L, columnList.size(), "查询表结构失败");
        TableColumn id = columnList.get(0);
        Assertions.assertEquals(dialectProperties.toCase("id"), id.getName(), "查询表结构失败");
        Assertions.assertEquals("主键自增", id.getComment(), "查询表结构失败");
        Assertions.assertTrue(id.getAutoIncrement(), "查询表结构失败");
        //Assertions.assertFalse(id.getNullable(), "查询表结构失败");
        Assertions.assertTrue(id.getPrimaryKey(), "查询表结构失败");

        TableColumn string = columnList.get(3);
        Assertions.assertEquals(dialectProperties.toCase("string"), string.getName(), "查询表结构失败");
        //Assertions.assertTrue(string.getNullable(), "查询表结构失败");
        Assertions.assertEquals("DATA", TestUtils.unWrapperDefaultValue(string.getDefaultValue()),
            "查询表结构失败");

        List<TableIndex> tableIndexList = tableService.queryIndexes(tableQueryParam);
        Assertions.assertEquals(4L, tableIndexList.size(), "查询表结构失败");
        Map<String, TableIndex> tableIndexMap = EasyCollectionUtils.toIdentityMap(tableIndexList,
            TableIndex::getName);
        TableIndex idxDate = tableIndexMap.get(dialectProperties.toCase(tableName + "_idx_date"));
        Assertions.assertEquals("日期索引", idxDate.getComment(), "查询表结构失败");
        Assertions.assertEquals(IndexTypeEnum.NORMAL.getCode(), idxDate.getType(), "查询表结构失败");
        Assertions.assertEquals(1L, idxDate.getColumnList().size(), "查询表结构失败");
        Assertions.assertEquals(dialectProperties.toCase("date"), idxDate.getColumnList().get(0).getColumnName(),
            "查询表结构失败");
        Assertions.assertEquals(CollationEnum.DESC.getCode(), idxDate.getColumnList().get(0).getCollation(),
            "查询表结构失败");

        TableIndex ukNumber = tableIndexMap.get(dialectProperties.toCase(tableName + "_uk_number"));
        Assertions.assertEquals("唯一索引", ukNumber.getComment(), "查询表结构失败");
        Assertions.assertEquals(IndexTypeEnum.UNIQUE.getCode(), ukNumber.getType(), "查询表结构失败");

        TableIndex idxNumberString = tableIndexMap.get(dialectProperties.toCase(tableName + "_idx_number_string"));
        Assertions.assertEquals(2, idxNumberString.getColumnList().size(), "查询表结构失败");
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
