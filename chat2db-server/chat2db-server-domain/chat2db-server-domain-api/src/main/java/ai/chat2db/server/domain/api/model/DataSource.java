package ai.chat2db.server.domain.api.model;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;

import ai.chat2db.spi.config.DriverConfig;
import ai.chat2db.spi.model.KeyValue;
import ai.chat2db.spi.model.SSHInfo;
import ai.chat2db.spi.model.SSLInfo;
import lombok.Data;
import org.springframework.util.ObjectUtils;

/**
 * @author moji
 * @version DataSourceDTO.java, v 0.1 2022年09月23日 15:39 moji Exp $
 * @date 2022/09/23
 */
@Data
public class DataSource {

    /**
     * 主键
     */
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    private LocalDateTime gmtModified;

    /**
     * 别名
     */
    private String alias;

    /**
     * 连接地址
     */
    private String url;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 数据库类型
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
    private Long environmentId;

    /**
     * 环境
     */
    private Environment environment;

    /**
     * 用户id
     */
    private Long userId;


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


    private boolean supportDatabase;

    private boolean supportSchema;

    public LinkedHashMap<String, Object> getExtendMap() {
        if (ObjectUtils.isEmpty(extendInfo)) {
            return new LinkedHashMap<>();
        }
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        for (KeyValue keyValue : extendInfo) {
            map.put(keyValue.getKey(), keyValue.getValue());
        }
        return map;
    }
}
