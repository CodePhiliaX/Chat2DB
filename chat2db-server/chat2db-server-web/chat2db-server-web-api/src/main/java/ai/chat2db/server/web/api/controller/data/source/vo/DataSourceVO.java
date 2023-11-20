package ai.chat2db.server.web.api.controller.data.source.vo;

import java.util.List;

import ai.chat2db.server.common.api.controller.vo.SimpleEnvironmentVO;
import ai.chat2db.spi.config.DriverConfig;
import ai.chat2db.spi.model.KeyValue;
import ai.chat2db.spi.model.SSHInfo;
import lombok.Data;

/**
 * @author moji
 * @version ConnectionVO.java, v 0.1 2022年09月16日 14:15 moji Exp $
 * @date 2022/09/16
 */
@Data
public class DataSourceVO {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 连接别名
     */
    private String alias;

    /**
     * 连接地址
     */
    private String url;

    /**
     * 连接用户
     */
    private String user;

    /**
     * password
     */
    private String password;

    /**
     * 认证类型
     */
    private String authenticationType;
    /**
     * 连接类型
     */
    private String type;

    /**
     * 环境类型
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

    ///**
    // * ssh
    // */
    //private SSLInfo ssl;

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
    private Long environmentId;

    /**
     * 环境
     */
    private SimpleEnvironmentVO environment;

    /**
     * 连接类型
     *
     * @see ai.chat2db.server.domain.api.enums.DataSourceKindEnum
     */
    private String kind;


    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务类型
     */
    private String serviceType;

    /**
     * 是否支持数据库
     */
    private boolean supportDatabase;

    /**
     * 是否支持schema
     */
    private boolean supportSchema;
}
