package ai.chat2db.server.domain.api.param.datasource;

import java.util.List;

import ai.chat2db.spi.config.DriverConfig;
import ai.chat2db.spi.model.KeyValue;
import ai.chat2db.spi.model.SSHInfo;
import ai.chat2db.spi.model.SSLInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author moji
 * @version DataSourceCreateParam.java, v 0.1 September 23, 2022 15:23 moji Exp $
 * @date 2022/09/23
 */
@Data
public class DataSourceUpdateParam {

    /**
     * primary key
     */
    @NotNull
    private Long id;

    /**
     * Alias
     */
    private String alias;

    /**
     * connection address
     */
    private String url;

    /**
     * userName
     */
    private String userName;

    /**
     * password
     */
    private String password;

    /**
     * Database type
     */
    private String type;

    /**
     * environment type
     */
    private String envType;

    /**
     * environment id
     */
    private Integer environmentId;

    /**
     * host
     */
    private String host;

    /**
     * port
     */
    private String port;

    /**
     * ssh
     */
    private SSHInfo ssh;

    /**
     * ssh
     */
    private SSLInfo ssl;

    /**
     * sid
     */
    private String sid;

    /**
     * driver
     */
    private String driver;


    /**
     * jdbc version
     */
    private String jdbc;

    /**
     * Extended Information
     */
    private List<KeyValue> extendInfo;

    /**
     * Driver configuration
     */
    private DriverConfig driverConfig;

    /**
     * service name
     */
    private String serviceName;

    /**
     * Service type
     */
    private String serviceType;

}
