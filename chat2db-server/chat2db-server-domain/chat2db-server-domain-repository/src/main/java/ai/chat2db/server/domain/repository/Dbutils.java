package ai.chat2db.server.domain.repository;

import ai.chat2db.server.domain.repository.entity.DataSourceDO;
import ai.chat2db.server.domain.repository.mapper.DataSourceMapper;
import ai.chat2db.server.tools.common.model.ConfigJson;
import ai.chat2db.server.tools.common.util.ConfigUtils;
import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Slf4j
public class Dbutils {

    private static final ThreadLocal<SqlSession> SQL_SESSION_THREAD_LOCAL = new ThreadLocal<>();

    public static void setSession() {
        SqlSession session = sqlSessionFactory.openSession(true);
        SQL_SESSION_THREAD_LOCAL.set(session);
    }

    public static void removeSession() {
        SqlSession session = SQL_SESSION_THREAD_LOCAL.get();
        if (session != null) {
            session.close();
        }
        SQL_SESSION_THREAD_LOCAL.remove();
    }

    private static SqlSessionFactory sqlSessionFactory;

    static {
        try {
            before();
        } catch (IOException e) {
            log.error("Dbutils error", e);
        }
    }


    private static void before() throws IOException {
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        //这是mybatis-plus的配置对象，对mybatis的Configuration进行增强
        MybatisConfiguration configuration = new MybatisConfiguration();
        //这是初始化配置，后面会添加这部分代码
        initConfiguration(configuration);
        //这是初始化连接器，如mybatis-plus的分页插件
        configuration.addInterceptor(initInterceptor());
        //配置日志实现
        configuration.setLogImpl(Slf4jImpl.class);
        //扫描mapper接口所在包
        configuration.addMappers("ai.chat2db.server.domain.repository.mapper");
        //构建mybatis-plus需要的globalconfig
        GlobalConfig globalConfig = GlobalConfigUtils.getGlobalConfig(configuration);
        //此参数会自动生成实现baseMapper的基础方法映射
        globalConfig.setSqlInjector(new DefaultSqlInjector());
        //设置id生成器
        globalConfig.setIdentifierGenerator(new DefaultIdentifierGenerator());
        //设置超类mapper
        globalConfig.setSuperMapperClass(BaseMapper.class);
        DataSource dataSource = initDataSource();
        Environment environment = new Environment("1", new JdbcTransactionFactory(), dataSource);
        configuration.setEnvironment(environment);
        //设置数据源
        registryMapperXml(configuration, "mapper");
        //构建sqlSessionFactory
        sqlSessionFactory = builder.build(configuration);

        initFlyway(dataSource);
        //创建session

    }

    private static void initFlyway(DataSource dataSource) {
        String currentVersion = ConfigUtils.getLocalVersion();
        ConfigJson configJson = ConfigUtils.getConfig();
        // Represents that the current version has been successfully launched
        if (StringUtils.isNotBlank(currentVersion) && configJson != null && StringUtils.equals(currentVersion,
                configJson.getLatestStartupSuccessVersion())) {
            return;
        }
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .load();
        flyway.migrate();
    }

    /**
     * 初始化配置
     *
     * @param configuration
     */
    private static void initConfiguration(MybatisConfiguration configuration) {
        //开启驼峰大小写转换
        configuration.setMapUnderscoreToCamelCase(true);
        //配置添加数据自动返回数据主键
        configuration.setUseGeneratedKeys(true);
    }

    /**
     * 初始化数据源
     *
     * @return
     */
    private static DataSource initDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        String environment = StringUtils.defaultString(System.getProperty("spring.profiles.active"), "dev");
        if ("dev".equalsIgnoreCase(environment)) {
            dataSource.setJdbcUrl("jdbc:h2:file:~/.chat2db/db/chat2db_dev;MODE=MYSQL");
        } else {
            dataSource.setJdbcUrl("jdbc:h2:~/.chat2db/db/chat2db;MODE=MYSQL;FILE_LOCK=NO");
        }
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setIdleTimeout(60000);
        dataSource.setAutoCommit(true);
        dataSource.setMaximumPoolSize(500);
        dataSource.setMinimumIdle(1);
        dataSource.setMaxLifetime(60000 * 10);
        dataSource.setConnectionTestQuery("SELECT 1");
        return dataSource;

    }

    /**
     * 初始化拦截器
     *
     * @return
     */
    private static Interceptor initInterceptor() {
        //创建mybatis-plus插件对象
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //构建分页插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setDbType(DbType.H2);
        paginationInnerInterceptor.setOverflow(true);
        paginationInnerInterceptor.setMaxLimit(2000L);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;
    }

    /**
     * 解析mapper.xml文件
     *
     * @param configuration
     * @param classPath
     * @throws IOException
     */
    private static void registryMapperXml(MybatisConfiguration configuration, String classPath) throws IOException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> mapper = contextClassLoader.getResources(classPath);
        while (mapper.hasMoreElements()) {
            URL url = mapper.nextElement();
            if (url.getProtocol().equals("file")) {
                String path = url.getPath();
                File file = new File(path);
                File[] files = file.listFiles();
                for (File f : files) {
                    FileInputStream in = new FileInputStream(f);
                    XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(in, configuration, f.getPath(), configuration.getSqlFragments());
                    xmlMapperBuilder.parse();
                    in.close();
                }
            } else {
                JarURLConnection urlConnection = (JarURLConnection) url.openConnection();
                JarFile jarFile = urlConnection.getJarFile();
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    if (jarEntry.getName().endsWith(".xml")) {
                        InputStream in = jarFile.getInputStream(jarEntry);
                        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(in, configuration, jarEntry.getName(), configuration.getSqlFragments());
                        xmlMapperBuilder.parse();
                        in.close();
                    }
                }
            }
        }
    }

    public static <T> T getMapper(Class<T> clazz) {
        SqlSession session = SQL_SESSION_THREAD_LOCAL.get();
        return session.getMapper(clazz);
    }

//    public static void main(String[] args) {
//
//        ExecutorService e = Executors.newCachedThreadPool();
//        for (int i = 0; i < 20; i++) {
//            e.execute(() -> {
//                SqlSession session = sqlSessionFactory.openSession();
//                DataSourceMapper mapper = session.getMapper(DataSourceMapper.class);
//                DataSourceDO dataSourceDO = mapper.selectById(1);
//                session.close();
//                System.out.println(JSON.toJSONString(dataSourceDO));
//            });
//        }
//
//    }
}
