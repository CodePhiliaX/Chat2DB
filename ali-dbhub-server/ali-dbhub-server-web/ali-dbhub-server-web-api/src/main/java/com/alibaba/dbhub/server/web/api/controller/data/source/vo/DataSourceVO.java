package com.alibaba.dbhub.server.web.api.controller.data.source.vo;

import java.util.List;
import java.util.Map;

import com.alibaba.dbhub.server.domain.support.model.KeyValue;
import com.alibaba.dbhub.server.domain.support.model.SSHInfo;
import com.alibaba.dbhub.server.domain.support.model.SSLInfo;

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
