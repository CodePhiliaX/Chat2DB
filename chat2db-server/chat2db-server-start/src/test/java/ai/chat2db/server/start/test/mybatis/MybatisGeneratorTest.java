package ai.chat2db.server.start.test.mybatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import ai.chat2db.server.start.test.common.BaseTest;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.google.common.collect.Lists;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

/**
 * Generate mapper of mybatis
 *
 * @author Jiaju Zhuang
 */
@Slf4j
public class MybatisGeneratorTest extends BaseTest {
    @Resource
    private DataSource dataSource;

    @Test
    public void coreGenerator() {

        HikariDataSource dataSource = new HikariDataSource();
        String environment = StringUtils.defaultString(System.getProperty("spring.profiles.active"), "dev");
        if ("dev".equalsIgnoreCase(environment)) {
            dataSource.setJdbcUrl("jdbc:h2:file:~/.chat2db/db/chat2db_dev;MODE=MYSQL");
        }else if ("test".equalsIgnoreCase(environment)) {
            dataSource.setJdbcUrl("jdbc:h2:file:~/.chat2db/db/chat2db_test;MODE=MYSQL");
        }else {
            dataSource.setJdbcUrl("jdbc:h2:~/.chat2db/db/chat2db;MODE=MYSQL;FILE_LOCK=NO");
        }
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setIdleTimeout(60000);
        dataSource.setAutoCommit(true);
        dataSource.setMaximumPoolSize(500);
        dataSource.setMinimumIdle(1);
        dataSource.setMaxLifetime(60000 * 10);
        dataSource.setConnectionTestQuery("SELECT 1");
        this.dataSource = dataSource;
        //doGenerator(Lists.newArrayList("data_source"));
        //doGenerator(Lists.newArrayList("operation_log"));
        //doGenerator(Lists.newArrayList("operation_saved"));
        //doGenerator(Lists.newArrayList("environment","data_source","team","team_dbhub_user","data_source_access",
        // "dbhub_user"));
        doGenerator(Lists.newArrayList("TASK"));
    }

    private void doGenerator(List<String> tableList) {

        // The current project address is the chat2db-server-start address.
        String outputDir = System.getProperty("user.dir")
            + "/../chat2db-server-domain/chat2db-server-domain-repository/src/main"
            + "/java";
        String xmlDir = System.getProperty("user.dir")
            + "/../chat2db-server-domain/chat2db-server-domain-repository/src/main"
            + "/resources/mapper";

        // Do not generate service controller
        Map<OutputFile, String> pathInfo = new HashMap<>();
        pathInfo.put(OutputFile.service, null);
        pathInfo.put(OutputFile.serviceImpl, null);
        pathInfo.put(OutputFile.xml, xmlDir);
        pathInfo.put(OutputFile.controller, null);

        FastAutoGenerator
            .create(new DataSourceConfig.Builder(dataSource)
                .typeConvert(new MySqlTypeConvert()))
            //Global configuration
            .globalConfig(builder -> {
                // Set author
                builder.author("chat2db")
                    //Do not open the folder after execution
                    .disableOpenDir()
                    // Or use date
                    .dateType(DateType.ONLY_DATE)
                    // Specify output directory
                    .outputDir(outputDir);
            })
            //Package configuration
            .packageConfig(builder -> {
                // Set parent package name
                builder.parent("ai.chat2db.server.domain.repository")
                    //Generate solid layer
                    .entity("entity")
                    .pathInfo(pathInfo)
                    //Generate mapper layer
                    .mapper("mapper");
            })
            //Policy configuration
            .strategyConfig(builder -> {
                // Set the table name to be generated
                builder.addInclude(tableList)
                    //Enable entity class configuration
                    .entityBuilder()
                    .formatFileName("%sDO")
                    .enableFileOverride()
                    //.addTableFills(new Column("gmt_create", FieldFill.INSERT)) // Table field filling
                    //.addTableFills(new Column("update_time", FieldFill.INSERT_UPDATE)) // Table field filling
                    //Turn on lombok
                    .enableLombok()
                    .mapperBuilder()
                    //// overwrite file
                    .enableFileOverride()
                ;

            })
            //Template configuration
            .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
            //execute
            .execute();
    }

}
