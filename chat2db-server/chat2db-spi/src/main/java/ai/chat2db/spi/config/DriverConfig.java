
package ai.chat2db.spi.config;

import java.util.List;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

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

    ///**
    // * name
    // */
    //private String name;

    /**
     * downloadJdbcDriverUrls
     */
    private List<String> downloadJdbcDriverUrls;


    private String dbType;

    /**
     * 自定义
     */
    private boolean custom;


    public boolean notEmpty() {
       return StringUtils.isNotBlank(getJdbcDriver()) && StringUtils.isNotBlank(
            getJdbcDriverClass());
    }
}