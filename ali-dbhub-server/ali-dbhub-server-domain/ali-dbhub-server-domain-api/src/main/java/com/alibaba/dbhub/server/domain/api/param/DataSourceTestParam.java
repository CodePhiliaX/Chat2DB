package com.alibaba.dbhub.server.domain.api.param;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.alibaba.dbhub.server.domain.support.enums.DbTypeEnum;
import com.alibaba.dbhub.server.domain.support.model.KeyValue;
import com.alibaba.dbhub.server.domain.support.model.SSHInfo;
import com.alibaba.dbhub.server.domain.support.model.SSLInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 数据源测试参数
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceTestParam {

    /**
     * 数据库类型
     *
     * @see DbTypeEnum
     */
    @NotNull
    private String dbType;

    /**
     * 请求连接
     */
    @NotNull
    private String url;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

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
