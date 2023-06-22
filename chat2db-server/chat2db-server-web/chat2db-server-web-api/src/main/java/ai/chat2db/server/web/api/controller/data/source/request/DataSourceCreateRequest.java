package ai.chat2db.server.web.api.controller.data.source.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;

import ai.chat2db.spi.model.KeyValue;
import ai.chat2db.spi.model.SSHInfo;
import ai.chat2db.spi.model.SSLInfo;
import ai.chat2db.server.tools.base.enums.EnvTypeEnum;

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
     * 连接类型
     * @see DbTypeEnum
     */
    @NotNull
    private String type;

    /**
     * 环境类型
     * @see EnvTypeEnum
     */
    private String envType;

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
