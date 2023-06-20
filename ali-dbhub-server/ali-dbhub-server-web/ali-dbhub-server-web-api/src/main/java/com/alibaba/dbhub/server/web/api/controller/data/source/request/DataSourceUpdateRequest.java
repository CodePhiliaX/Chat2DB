package com.alibaba.dbhub.server.web.api.controller.data.source.request;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.alibaba.dbhub.server.domain.support.enums.DbTypeEnum;
import com.alibaba.dbhub.server.domain.support.model.KeyValue;
import com.alibaba.dbhub.server.domain.support.model.SSHInfo;
import com.alibaba.dbhub.server.domain.support.model.SSLInfo;
import com.alibaba.dbhub.server.tools.base.enums.EnvTypeEnum;

import lombok.Data;

/**
 * @author moji
 * @version ConnectionCreateRequest.java, v 0.1 2022年09月16日 14:23 moji Exp $
 * @date 2022/09/16
 */
@Data
public class DataSourceUpdateRequest {

    /**
     * 主键id
     */
    @NotNull
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
     * 密码
     */
    private String password;

    /**
     * 连接类型
     * @see DbTypeEnum
     */
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
