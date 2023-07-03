
package ai.chat2db.spi.config;

import java.util.List;

import lombok.Data;

/**
 * @author jipengfei
 * @version : DriverConfig.java
 */
@Data
public class DriverConfig {
    /**
     * jdbcDriver
     */
    private String jdbcDriver;

    /**
     * jdbcDriverClass
     */
    private String jdbcDriverClass;

    /**
     * name
     */
    private String name;

    /**
     * downloadJdbcDriverUrls
     */
    private List<String> downloadJdbcDriverUrls;

}