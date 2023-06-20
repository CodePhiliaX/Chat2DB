package com.alibaba.dbhub.server.domain.api.param;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.alibaba.dbhub.server.domain.support.model.KeyValue;
import com.alibaba.dbhub.server.domain.support.model.SSHInfo;
import com.alibaba.dbhub.server.domain.support.model.SSLInfo;

import lombok.Data;

/**
 * @author moji
 * @version DataSourceCreateParam.java, v 0.1 2022年09月23日 15:23 moji Exp $
 * @date 2022/09/23
 */
@Data
public class DataSourceUpdateParam {

    /**
     * 主键
     */
    @NotNull
    private Long id;

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

}
