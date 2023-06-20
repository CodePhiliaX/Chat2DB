/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alibaba.dbhub.server.domain.support.model;

import lombok.Data;

/**
 * @author jipengfei
 * @version : SSHInfo.java
 */
@Data
public class SSHInfo {

    /**
     * 是否使用ssh
     */
    private boolean use;

    /**
     * ssh主机名
     */
    private String hostName;

    /**
     * ssh端口
     */
    private String port;

    /**
     * ssh用户名
     */
    private String userName;

    /**
     * ssh本地端口
     */
    private String localPort;

    /**
     * ssh认证类型
     */
    private String authenticationType;

    /**
     * ssh密码
     */
    private String password;

    /**
     * ssh密钥文件
     */
    private String keyFile;

    /**
     * ssh密钥文件密码
     */
    private String passphrase;

    /**
     * ssh跳板机目标主机
     */
    private String rHost;

    /**
     * ssh跳板目标端口
     */
    private String rPort;

}