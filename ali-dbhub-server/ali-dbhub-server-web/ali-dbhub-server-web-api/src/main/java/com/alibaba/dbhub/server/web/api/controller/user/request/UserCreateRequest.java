/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alibaba.dbhub.server.web.api.controller.user.request;

import java.io.Serial;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jipengfei
 * @version : UserCreateRequest.java
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 353710386092262213L;
    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickName;


    /**
     * 邮箱
     */
    private String email;
}