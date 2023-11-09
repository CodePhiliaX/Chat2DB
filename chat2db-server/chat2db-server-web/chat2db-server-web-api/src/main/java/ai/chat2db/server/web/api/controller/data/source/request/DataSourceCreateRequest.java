package ai.chat2db.server.web.api.controller.data.source.request;

import java.util.List;

import ai.chat2db.spi.config.DriverConfig;
import jakarta.validation.constraints.NotNull;

import ai.chat2db.spi.model.KeyValue;
import ai.chat2db.spi.model.SSHInfo;
import ai.chat2db.spi.model.SSLInfo;

import lombok.Data;

/**
 * @author moji
 * @version ConnectionCreateRequest.java, v 0.1 2022年09月16日 14:23 moji Exp $
 * @date 2022/09/16
 */
@Data
public class DataSourceCreateRequest {

    /**
     * 连接别名
     */
    private String alias;

    /**
     * 连接地址
     */
    @NotNull
    private String url;

    /**
     * 连接用户名
     */
    private String user;

    /**
     * 密码
     */
    @NotNull
    private String password;

    /**
     * 认证类型
     */
    private String authenticationType;

    /**
     * 连接类型
     */
    @NotNull
    private String type;

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
     * jdbc版本
     */
    private String jdbc;
    /**
     * 扩展信息
     */
    private List<KeyValue> extendInfo;


    /**
     * 驱动配置
     */
    private DriverConfig driverConfig;


    /**
     * 环境id
     */
    @NotNull
    private Long environmentId;



    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务类型
     */
    private String serviceType;

}
