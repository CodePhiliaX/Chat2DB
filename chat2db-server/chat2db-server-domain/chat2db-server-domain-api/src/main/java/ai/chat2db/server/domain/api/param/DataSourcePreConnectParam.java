package ai.chat2db.server.domain.api.param;

import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotNull;

import ai.chat2db.spi.model.*;

import lombok.Data;

/**
 * @author moji
 * @version ConnectionCreateRequest.java, v 0.1 2022年09月16日 14:23 moji Exp $
 * @date 2022/09/16
 */
@Data
public class DataSourcePreConnectParam {

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
     * 连接用户
     */
    private String user;

    /**
     * 密码
     */
    @NotNull
    private String password;

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
}
