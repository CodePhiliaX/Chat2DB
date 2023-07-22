
package ai.chat2db.server.start.config.config;

import java.util.ArrayList;
import java.util.List;

import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.util.JdbcJarUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author jipengfei
 * @version : JarDownloadTask.java
 */
@Component
@Slf4j
public class JarDownloadTask implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        List<String> urls = new ArrayList<>();
        Chat2DBContext.PLUGIN_MAP.forEach((k, v) -> {
            v.getDBConfig().getDriverConfigList().forEach(driverConfig -> {
                if (driverConfig != null && !CollectionUtils.isEmpty(driverConfig.getDownloadJdbcDriverUrls()) && (
                    "MYSQL".equals(driverConfig.getDbType()))) {
                    urls.addAll(driverConfig.getDownloadJdbcDriverUrls());
                }
            });
        });
        JdbcJarUtils.asyncDownload(urls);
    }
}