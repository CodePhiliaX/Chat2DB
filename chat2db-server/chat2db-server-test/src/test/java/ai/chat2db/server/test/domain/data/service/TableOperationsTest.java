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
 * Data source testing
 *
 * @author Jiaju Zhuang
 */
@Slf4j
public class TableOperationsTest extends BaseTest {
    /**
     * Table Name
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

            // Create table structure

            DlExecuteParam templateQueryParam = new DlExecuteParam();
            templateQueryParam.setConsoleId(consoleId);
            templateQueryParam.setDataSourceId(dataSourceId);
            templateQueryParam.setSql(dialectProperties.getCrateTableSql(TABLE_NAME));
            dlTemplateService.execute(templateQueryParam);
            // Query table creation statement
            ShowCreateTableParam showCreateTableParam = ShowCreateTableParam.builder()
                .dataSourceId(dataSourceId)
                .databaseName(dialectProperties.getDatabaseName())
                .tableName(dialectProperties.toCase(TABLE_NAME))
                .build();
            if (dialectProperties.getDbType() == "POSTGRESQL") {
                showCreateTableParam.setSchemaName("public");
            }

            DataResult<String> createTable = tableService.showCreateTable(showCreateTableParam);
            log.info("Table creation statement: {}", createTable.getData());
            if (dialectProperties.getDbType() != "H2") {
                Assertions.assertTrue(createTable.getData().contains(dialectProperties.toCase(TABLE_NAME)),
                    "Query table structure failed");
            }

            //  Query table structure
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
            log.info("Analyzing data returns {}", JSON.toJSONString(tableList));
            Assertions.assertNotEquals(0L, tableList.size(), "Query table structure failed");
            Table table = tableList.get(0);
            // Assertions.assertEquals(dialectProperties.toCase(TABLE_NAME), table.getName(), "Query table structure failed");
            if (dialectProperties.getDbType() != "POSTGRESQL") {
                Assertions.assertEquals("Test table", table.getComment(), "Query table structure failed");
            }
            TableQueryParam tableQueryParam = new TableQueryParam();
            tableQueryParam.setTableName(table.getName());
            tableQueryParam.setDataSourceId(dataSourceId);
            tableQueryParam.setDatabaseName(dialectProperties.getDatabaseName());
            if (dialectProperties.getDbType() == "POSTGRESQL") {
                tableQueryParam.setSchemaName("public");
            }
            List<TableColumn> columnList = tableService.queryColumns(tableQueryParam);
            Assertions.assertEquals(4L, columnList.size(), "Query table structure failed");
            TableColumn id = columnList.get(0);
            Assertions.assertEquals(dialectProperties.toCase("id"), id.getName(), "Query table structure failed");
            Assertions.assertEquals("Primary key auto-increment", id.getComment(), "Query table structure failed");
            Assertions.assertTrue(id.getAutoIncrement(), "Query table structure failed");
            //Assertions.assertFalse(id.getNullable(), "Query table structure failed");

            TableColumn string = columnList.get(3);
            Assertions.assertEquals(dialectProperties.toCase("string"), string.getName(), "Query table structure failed");
            //Assertions.assertTrue(string.getNullable(), "Query table structure failed");
            Assertions.assertEquals("DATA", TestUtils.unWrapperDefaultValue(string.getDefaultValue()),
                "Query table structure failed");
            if (dialectProperties.getDbType() == "POSTGRESQL") {
                tablePageQueryParam.setSchemaName("public");
            }
            List<TableIndex> tableIndexList = tableService.queryIndexes(tableQueryParam);
            log.info("Analyzing data returns {}", JSON.toJSONString(tableIndexList));
            Assertions.assertEquals(4L, tableIndexList.size(), "Query table structure failed");
            Map<String, TableIndex> tableIndexMap = EasyCollectionUtils.toIdentityMap(tableIndexList,
                TableIndex::getName);
            TableIndex idxDate = tableIndexMap.get(dialectProperties.toCase(TABLE_NAME + "_idx_date"));
            Assertions.assertEquals("date index", idxDate.getComment(), "Query table structure failed");
            Assertions.assertEquals(IndexTypeEnum.NORMAL.getCode(), idxDate.getType(), "Query table structure failed");
            Assertions.assertEquals(1L, idxDate.getColumnList().size(), "Query table structure failed");
            Assertions.assertEquals(dialectProperties.toCase("date"), idxDate.getColumnList().get(0).getColumnName(),
                "Query table structure failed");
            Assertions.assertEquals(CollationEnum.DESC.getCode(), idxDate.getColumnList().get(0).getCollation(),
                "Query table structure failed");

            TableIndex ukNumber = tableIndexMap.get(dialectProperties.toCase(TABLE_NAME + "_uk_number"));
            Assertions.assertEquals("unique index", ukNumber.getComment(), "Query table structure failed");
            Assertions.assertEquals(IndexTypeEnum.UNIQUE.getCode(), ukNumber.getType(), "Query table structure failed");

            TableIndex idxNumberString = tableIndexMap.get(dialectProperties.toCase(TABLE_NAME + "_idx_number_string"));
            Assertions.assertEquals(2, idxNumberString.getColumnList().size(), "Query table structure failed");

            // Delete table structure
            DropParam dropParam = DropParam.builder()
                .dataSourceId(dataSourceId)
                .databaseName(dialectProperties.getDatabaseName())
                .name(dialectProperties.toCase(TABLE_NAME))
                .build();
            tableService.drop(dropParam);
            //  Query table structure
            tablePageQueryParam = new TablePageQueryParam();
            tablePageQueryParam.setDataSourceId(dataSourceId);
            tablePageQueryParam.setDatabaseName(dialectProperties.getDatabaseName());
            tablePageQueryParam.setTableName(dialectProperties.toCase(TABLE_NAME));
            tableList = tableService.pageQuery(tablePageQueryParam, TableSelector.builder()
                .columnList(Boolean.TRUE)
                .indexList(Boolean.TRUE)
                .build()).getData();
            log.info("After deleting the table, the data returns {}", JSON.toJSONString(tableList));
            Assertions.assertEquals(0L, tableList.size(), "Query table structure failed");

            // Test the table creation statement
            testBuildSql(dialectProperties, dataSourceId, consoleId);

            removeConnect();
        }

    }

    private void testBuildSql(DialectProperties dialectProperties, Long dataSourceId, Long consoleId) {
        if (dialectProperties.getDbType() != "MYSQL") {
            log.error("Currently the test case only supports mysql");
            return;
        }
        // Create new table
        //    CREATE TABLE `DATA_OPS_TEMPLATE_TEST_1673093980449`
        //    (
        //    `id`     bigint PRIMARY KEY AUTO_INCREMENT NOT NULL COMMENT 'Primary key auto-increment',
        //    `date`   datetime(3)                          not null COMMENT 'date',
        //    `number` bigint COMMENT 'long integer',
        //    `string` VARCHAR(100) default 'DATA' COMMENT 'name',
        //        index DATA_OPS_TEMPLATE_TEST_1673093980449_idx_date (date desc) comment 'date index',
        //        unique DATA_OPS_TEMPLATE_TEST_1673093980449_uk_number (number) comment 'unique index',
        //        index DATA_OPS_TEMPLATE_TEST_1673093980449_idx_number_string (number, date) comment 'Union index'
        //) COMMENT ='Test table';
        //        * The case depends on the specific database:
        //* Create table structure: Test table
        //       * Fields:
        //* id   Primary key auto-increment
        //* date date is not null
        //       * number long integer
        //       * string  String length 100 default value "DATA"
        //       *
        //* Index (plus $tableName_ because some database indexes are globally unique):
        //* $tableName_idx_date date index reverse order
        //       * $tableName_uk_number unique index
        //       * $tableName_idx_number_string Union index
        String tableName = dialectProperties.toCase("data_ops_table_test_" + System.currentTimeMillis());
        Table newTable = new Table();
        newTable.setName(tableName);
        newTable.setComment("Test table");
        List<TableColumn> tableColumnList = new ArrayList<>();
        newTable.setColumnList(tableColumnList);
        //id
        TableColumn idTableColumn = new TableColumn();
        idTableColumn.setName("id");
        idTableColumn.setAutoIncrement(Boolean.TRUE);
        idTableColumn.setPrimaryKey(Boolean.TRUE);
        //idTableColumn.setNullable(Boolean.FALSE);
        idTableColumn.setComment("Primary key auto-increment");
        idTableColumn.setColumnType("bigint");
        tableColumnList.add(idTableColumn);

        // date
        TableColumn dateTableColumn = new TableColumn();
        dateTableColumn.setName("date");
        //dateTableColumn.setNullable(Boolean.FALSE);
        dateTableColumn.setComment("date");
        dateTableColumn.setColumnType("datetime(3)");
        tableColumnList.add(dateTableColumn);

        // number
        TableColumn numberTableColumn = new TableColumn();
        numberTableColumn.setName("number");
        numberTableColumn.setComment("long integer");
        numberTableColumn.setColumnType("bigint");
        tableColumnList.add(numberTableColumn);

        // string
        TableColumn stringTableColumn = new TableColumn();
        stringTableColumn.setName("string");
        stringTableColumn.setComment("name");
        stringTableColumn.setColumnType("varchar(100)");
        stringTableColumn.setDefaultValue("DATA");
        tableColumnList.add(stringTableColumn);

        // index
        List<TableIndex> tableIndexList = new ArrayList<>();
        newTable.setIndexList(tableIndexList);

        //        index DATA_OPS_TEMPLATE_TEST_1673093980449_idx_date (date desc) comment 'date index',
        tableIndexList.add(TableIndex.builder()
            .name(tableName + "_idx_date")
            .type(IndexTypeEnum.NORMAL.getCode())
            .comment("date index")
            .columnList(Lists.newArrayList(TableIndexColumn.builder()
                .columnName("date")
                .collation(CollationEnum.DESC.getCode())
                .build()))
            .build());

        //        unique DATA_OPS_TEMPLATE_TEST_1673093980449_uk_number (number) comment 'unique index',
        tableIndexList.add(TableIndex.builder()
            .name(tableName + "_uk_number")
            .type(IndexTypeEnum.UNIQUE.getCode())
            .comment("unique index")
            .columnList(Lists.newArrayList(TableIndexColumn.builder()
                .columnName("number")
                .build()))
            .build());
        //        index DATA_OPS_TEMPLATE_TEST_1673093980449_idx_number_string (number, date) comment 'Union index'
        tableIndexList.add(TableIndex.builder()
            .name(tableName + "_idx_number_string")
            .type(IndexTypeEnum.NORMAL.getCode())
            .comment("Union index")
            .columnList(Lists.newArrayList(TableIndexColumn.builder()
                    .columnName("number")
                    .build(),
                TableIndexColumn.builder()
                    .columnName("date")
                    .build()))
            .build());
        // build sql
        List<Sql> buildTableSqlList = tableService.buildSql(null, newTable).getData();
        log.info("The structural statement to create a table is:{}", JSON.toJSONString(buildTableSqlList));
        for (Sql sql : buildTableSqlList) {
            DlExecuteParam templateQueryParam = new DlExecuteParam();
            templateQueryParam.setConsoleId(consoleId);
            templateQueryParam.setDataSourceId(dataSourceId);
            templateQueryParam.setSql(sql.getSql());
            dlTemplateService.execute(templateQueryParam);
        }

        // Check table structure
        checkTable(tableName, dialectProperties, dataSourceId);

        //  Go to the database to query the table structure
        TableQueryParam tablePageQueryParam = new TableQueryParam();
        tablePageQueryParam.setDataSourceId(dataSourceId);
        tablePageQueryParam.setDatabaseName(dialectProperties.getDatabaseName());
        tablePageQueryParam.setTableName(dialectProperties.toCase(tableName));
        Table table = tableService.query(tablePageQueryParam, TableSelector.builder()
            .columnList(Boolean.TRUE)
            .indexList(Boolean.TRUE)
            .build()).getData();
        log.info("Analyzing data returns {}", JSON.toJSONString(table));
        Assertions.assertNotNull(table, "Query table structure failed");
        Table oldTable = table;
        Assertions.assertEquals(dialectProperties.toCase(tableName), oldTable.getName(), "Query table structure failed");
        Assertions.assertEquals("Test table", oldTable.getComment(), "Query table structure failed");

        // Modify table structure
        // build sql
        log.info("oldTable：{}", JSON.toJSONString(oldTable));
        log.info("newTable：{}", JSON.toJSONString(newTable));
        buildTableSqlList = tableService.buildSql(oldTable, newTable).getData();
        log.info("Modify the table structure: {}", JSON.toJSONString(buildTableSqlList));
        Assertions.assertTrue(!buildTableSqlList.isEmpty(), "构建sql失败");
        //  Let’s query again. There will be 2 objects.
        tablePageQueryParam = new TableQueryParam();
        tablePageQueryParam.setDataSourceId(dataSourceId);
        tablePageQueryParam.setDatabaseName(dialectProperties.getDatabaseName());
        tablePageQueryParam.setTableName(dialectProperties.toCase(tableName));
        newTable = tableService.query(tablePageQueryParam, TableSelector.builder()
            .columnList(Boolean.TRUE)
            .indexList(Boolean.TRUE)
            .build()).getData();

        // Modify fields

        // Add a new field
        newTable.getColumnList().add(TableColumn.builder()
            .name("add_string")
            .columnType("varchar(20)")
            .comment("New string")
            .build());

        // Add a new index
        newTable.getIndexList().add(TableIndex.builder()
            .name(tableName + "_idx_string_new")
            .type(IndexTypeEnum.NORMAL.getCode())
            .comment("new string index")
            .columnList(Lists.newArrayList(TableIndexColumn.builder()
                .columnName("add_string")
                .collation(CollationEnum.DESC.getCode())
                .build()))
            .build());

        // Query table structure changes
        log.info("oldTable：{}", JSON.toJSONString(oldTable));
        log.info("newTable：{}", JSON.toJSONString(newTable));
        buildTableSqlList = tableService.buildSql(oldTable, newTable).getData();
        log.info("Modify the table structure: {}", JSON.toJSONString(buildTableSqlList));

        // Delete table structure
        dropTable(tableName, dialectProperties, dataSourceId);
    }

    private void dropTable(String tableName, DialectProperties dialectProperties, Long dataSourceId) {
        // Delete table structure
        DropParam dropParam = DropParam.builder()
            .dataSourceId(dataSourceId)
            .databaseName(dialectProperties.getDatabaseName())
            .name(dialectProperties.toCase(tableName))
            .build();
        tableService.drop(dropParam);
        //  Query table structure
        TablePageQueryParam tablePageQueryParam = new TablePageQueryParam();
        tablePageQueryParam.setDataSourceId(dataSourceId);
        tablePageQueryParam.setDatabaseName(dialectProperties.getDatabaseName());
        tablePageQueryParam.setTableName(dialectProperties.toCase(tableName));
        List<Table> tableList = tableService.pageQuery(tablePageQueryParam, TableSelector.builder()
            .columnList(Boolean.TRUE)
            .indexList(Boolean.TRUE)
            .build()).getData();
        log.info("After deleting the table, the data returns {}", JSON.toJSONString(tableList));
        Assertions.assertEquals(0L, tableList.size(), "Query table structure failed");
    }

    private void checkTable(String tableName, DialectProperties dialectProperties, Long dataSourceId) {
        //  Query table structure
        TablePageQueryParam tablePageQueryParam = new TablePageQueryParam();
        tablePageQueryParam.setDataSourceId(dataSourceId);
        tablePageQueryParam.setDatabaseName(dialectProperties.getDatabaseName());
        tablePageQueryParam.setTableName(dialectProperties.toCase(tableName));
        List<Table> tableList = tableService.pageQuery(tablePageQueryParam, TableSelector.builder()
            .columnList(Boolean.TRUE)
            .indexList(Boolean.TRUE)
            .build()).getData();

        log.info("Analyzing data returns {}", JSON.toJSONString(tableList));
        Assertions.assertEquals(1L, tableList.size(), "Query table structure failed");
        Table table = tableList.get(0);
        Assertions.assertEquals(dialectProperties.toCase(tableName), table.getName(), "Query table structure failed");
        Assertions.assertEquals("Test table", table.getComment(), "Query table structure failed");
        TableQueryParam tableQueryParam = new TableQueryParam();
        tableQueryParam.setTableName(table.getName());
        tableQueryParam.setDataSourceId(dataSourceId);
        tableQueryParam.setDatabaseName(dialectProperties.getDatabaseName());

        List<TableColumn> columnList = tableService.queryColumns(tableQueryParam);
        Assertions.assertEquals(4L, columnList.size(), "Query table structure failed");
        TableColumn id = columnList.get(0);
        Assertions.assertEquals(dialectProperties.toCase("id"), id.getName(), "Query table structure failed");
        Assertions.assertEquals("Primary key auto-increment", id.getComment(), "Query table structure failed");
        Assertions.assertTrue(id.getAutoIncrement(), "Query table structure failed");
        //Assertions.assertFalse(id.getNullable(), "Query table structure failed");
        Assertions.assertTrue(id.getPrimaryKey(), "Query table structure failed");

        TableColumn string = columnList.get(3);
        Assertions.assertEquals(dialectProperties.toCase("string"), string.getName(), "Query table structure failed");
        //Assertions.assertTrue(string.getNullable(), "Query table structure failed");
        Assertions.assertEquals("DATA", TestUtils.unWrapperDefaultValue(string.getDefaultValue()),
            "Query table structure failed");

        List<TableIndex> tableIndexList = tableService.queryIndexes(tableQueryParam);
        Assertions.assertEquals(4L, tableIndexList.size(), "Query table structure failed");
        Map<String, TableIndex> tableIndexMap = EasyCollectionUtils.toIdentityMap(tableIndexList,
            TableIndex::getName);
        TableIndex idxDate = tableIndexMap.get(dialectProperties.toCase(tableName + "_idx_date"));
        Assertions.assertEquals("date index", idxDate.getComment(), "Query table structure failed");
        Assertions.assertEquals(IndexTypeEnum.NORMAL.getCode(), idxDate.getType(), "Query table structure failed");
        Assertions.assertEquals(1L, idxDate.getColumnList().size(), "Query table structure failed");
        Assertions.assertEquals(dialectProperties.toCase("date"), idxDate.getColumnList().get(0).getColumnName(),
            "Query table structure failed");
        Assertions.assertEquals(CollationEnum.DESC.getCode(), idxDate.getColumnList().get(0).getCollation(),
            "Query table structure failed");

        TableIndex ukNumber = tableIndexMap.get(dialectProperties.toCase(tableName + "_uk_number"));
        Assertions.assertEquals("unique index", ukNumber.getComment(), "Query table structure failed");
        Assertions.assertEquals(IndexTypeEnum.UNIQUE.getCode(), ukNumber.getType(), "Query table structure failed");

        TableIndex idxNumberString = tableIndexMap.get(dialectProperties.toCase(tableName + "_idx_number_string"));
        Assertions.assertEquals(2, idxNumberString.getColumnList().size(), "Query table structure failed");
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
