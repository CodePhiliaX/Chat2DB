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
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * 生成mybatis 的mapper
 *
 * @author Jiaju Zhuang
 */
@Slf4j
public class MybatisGeneratorTest extends BaseTest {
    @Resource
    private DataSource dataSource;

    @Test
    public void coreGenerator() {
        //doGenerator(Lists.newArrayList("data_source"));
        //doGenerator(Lists.newArrayList("operation_log"));
        //doGenerator(Lists.newArrayList("operation_saved"));
        //doGenerator(Lists.newArrayList("environment","data_source","team","team_dbhub_user","data_source_access",
        // "dbhub_user"));
        doGenerator(Lists.newArrayList("operation_log"));
    }

    private void doGenerator(List<String> tableList) {

        // 当前项目地址 拿到的是chat2db-server-start地址
        String outputDir = System.getProperty("user.dir")
            + "/../chat2db-server-domain/chat2db-server-domain-repository/src/main"
            + "/java";
        String xmlDir = System.getProperty("user.dir")
            + "/../chat2db-server-domain/chat2db-server-domain-repository/src/main"
            + "/resources/mapper";

        // 不要生成service controller
        Map<OutputFile, String> pathInfo = new HashMap<>();
        pathInfo.put(OutputFile.service, null);
        pathInfo.put(OutputFile.serviceImpl, null);
        pathInfo.put(OutputFile.xml, xmlDir);
        pathInfo.put(OutputFile.controller, null);

        FastAutoGenerator
            .create(new DataSourceConfig.Builder(dataSource)
                .typeConvert(new MySqlTypeConvert()))
            //全局配置
            .globalConfig(builder -> {
                // 设置作者
                builder.author("chat2db")
                    //执行完毕不打开文件夹
                    .disableOpenDir()
                    // 还是使用date
                    .dateType(DateType.ONLY_DATE)
                    // 指定输出目录
                    .outputDir(outputDir);
            })
            //包配置
            .packageConfig(builder -> {
                // 设置父包名
                builder.parent("ai.chat2db.server.domain.repository")
                    //生成实体层
                    .entity("entity")
                    .pathInfo(pathInfo)
                    //生成mapper层
                    .mapper("mapper");
            })
            //策略配置
            .strategyConfig(builder -> {
                // 设置需要生成的表名
                builder.addInclude(tableList)
                    //开启实体类配置
                    .entityBuilder()
                    .formatFileName("%sDO")
                    .enableFileOverride()
                    //.addTableFills(new Column("gmt_create", FieldFill.INSERT)) // 表字段填充
                    //.addTableFills(new Column("update_time", FieldFill.INSERT_UPDATE)) // 表字段填充
                    //开启lombok
                    .enableLombok()
                    .mapperBuilder()
                    //// 覆盖文件
                    .enableFileOverride()
                ;

            })
            //模板配置
            .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
            //执行
            .execute();
    }

}
