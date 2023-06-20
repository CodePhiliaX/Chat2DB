/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alibaba.dbhub.server.domain.support.sql;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import com.alibaba.dbhub.server.domain.support.enums.DbTypeEnum;
import com.alibaba.dbhub.server.domain.support.model.KeyValue;
import com.alibaba.dbhub.server.domain.support.model.SSHInfo;
import com.alibaba.dbhub.server.domain.support.model.SSLInfo;

import com.jcraft.jsch.Session;
import org.springframework.util.ObjectUtils;

/**
 * @author jipengfei
 * @version : ConnectInfo.java
 */
public class ConnectInfo {
    /**
     * 别名
     */
    private String alias;
    /**
     * 数据连接ID
     */
    private Long dataSourceId;


    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    private LocalDateTime gmtModified;
    /**
     * database
     */
    private String databaseName;

    /**
     * 控制台ID
     */
    private Long consoleId;

    /**
     * 数据库URL
     */
    private String url;

    /**
     * 用户名
     */
    private String user;

    /**
     * 密码
     */
    private String password;

    /**
     * console独立占有连接
     */
    private Boolean consoleOwn = Boolean.FALSE;

    /**
     * 数据库类型
     */
    private DbTypeEnum dbType;

    private Integer port;

    /**
     *
     */
    private String urlWithOutDatabase;

    /**
     * host
     */
    private String host;

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



    public Connection connection;

    /**
     * Getter method for property <tt>session</tt>.
     *
     * @return property value of session
     */
    public Session getSession() {
        return session;
    }

    /**
     * Setter method for property <tt>session</tt>.
     *
     * @param session value to be assigned to property session
     */
    public void setSession(Session session) {
        this.session = session;
    }

    public Session session;


    /**
     * Getter method for property <tt>extendInfo</tt>.
     *
     * @return property value of extendInfo
     */
    public LinkedHashMap<String,Object> getExtendMap() {
        if (ObjectUtils.isEmpty(extendInfo)) {
            return new LinkedHashMap<>();
        }
        LinkedHashMap<String,Object> map = new LinkedHashMap<>();
        for (KeyValue keyValue : extendInfo) {
            map.put(keyValue.getKey(),keyValue.getValue());
        }
        return map;
    }


    public void setDatabase(String database) {
        this.databaseName = database;
        //if (!ObjectUtils.isEmpty(this.urlWithOutDatabase) && !ObjectUtils.isEmpty(this.databaseName)) {
        //    this.url = this.urlWithOutDatabase + "/" + database;
        //}
    }

    public String key() {
        return this.dataSourceId + "_" + this.databaseName;
    }

    public void setUrl(String url) {
        this.url = url;
        //if (this.dbType != DbTypeEnum.MYSQL && this.dbType != DbTypeEnum.POSTGRESQL) {
        //    return;
        //}
        //if (!ObjectUtils.isEmpty(url)) {
        //    //jdbc:postgresql://localhost:5432/postgres
        //    String[] array = getUrl().split(":");
        //    if (array.length == 4) {
        //        String str = array[3];
        //        boolean isDigit = true;
        //        StringBuffer sb = new StringBuffer();
        //        StringBuffer sb1 = new StringBuffer();
        //        for (int i = 0; i < str.length(); i++) {
        //            char c = str.charAt(i);
        //            if (isDigit == true) {
        //                if (!Character.isDigit(c)) {
        //                    isDigit = false;
        //                } else {
        //                    sb1.append(c);
        //                }
        //            } else {
        //                sb.append(c);
        //            }
        //        }
        //        String s = sb.toString();
        //        if (!ObjectUtils.isEmpty(s)) {
        //            this.databaseName = s;
        //        }
        //        this.port = Integer.parseInt(sb1.toString());
        //        this.urlWithOutDatabase = array[0] + ":" + array[1] + ":" + array[2] + ":" + port;
        //    }
        //}
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (!(o instanceof ConnectInfo)) {return false;}
        ConnectInfo that = (ConnectInfo)o;
        return Objects.equals(dataSourceId, that.dataSourceId)
            && Objects.equals(gmtModified, that.gmtModified)
            ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataSourceId, consoleId, databaseName);
    }

    /**
     * Getter method for property <tt>dataSourceId</tt>.
     *
     * @return property value of dataSourceId
     */
    public Long getDataSourceId() {
        return dataSourceId;
    }

    /**
     * Setter method for property <tt>dataSourceId</tt>.
     *
     * @param dataSourceId value to be assigned to property dataSourceId
     */
    public void setDataSourceId(Long dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    /**
     * Getter method for property <tt>databaseName</tt>.
     *
     * @return property value of databaseName
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * Setter method for property <tt>databaseName</tt>.
     *
     * @param databaseName value to be assigned to property databaseName
     */
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    /**
     * Getter method for property <tt>consoleId</tt>.
     *
     * @return property value of consoleId
     */
    public Long getConsoleId() {
        return consoleId;
    }

    /**
     * Setter method for property <tt>consoleId</tt>.
     *
     * @param consoleId value to be assigned to property consoleId
     */
    public void setConsoleId(Long consoleId) {
        this.consoleId = consoleId;
    }

    /**
     * Getter method for property <tt>url</tt>.
     *
     * @return property value of url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Getter method for property <tt>user</tt>.
     *
     * @return property value of user
     */
    public String getUser() {
        return user;
    }

    /**
     * Setter method for property <tt>user</tt>.
     *
     * @param user value to be assigned to property user
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Getter method for property <tt>password</tt>.
     *
     * @return property value of password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter method for property <tt>password</tt>.
     *
     * @param password value to be assigned to property password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter method for property <tt>consoleOwn</tt>.
     *
     * @return property value of consoleOwn
     */
    public Boolean getConsoleOwn() {
        return consoleOwn;
    }

    /**
     * Setter method for property <tt>consoleOwn</tt>.
     *
     * @param consoleOwn value to be assigned to property consoleOwn
     */
    public void setConsoleOwn(Boolean consoleOwn) {
        this.consoleOwn = consoleOwn;
    }

    /**
     * Getter method for property <tt>dbType</tt>.
     *
     * @return property value of dbType
     */
    public DbTypeEnum getDbType() {
        return dbType;
    }

    /**
     * Setter method for property <tt>dbType</tt>.
     *
     * @param dbType value to be assigned to property dbType
     */
    public void setDbType(DbTypeEnum dbType) {
        this.dbType = dbType;
    }

    /**
     * Getter method for property <tt>port</tt>.
     *
     * @return property value of port
     */
    public Integer getPort() {
        return port;
    }

    /**
     * Setter method for property <tt>port</tt>.
     *
     * @param port value to be assigned to property port
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * Getter method for property <tt>urlWithOutDatabase</tt>.
     *
     * @return property value of urlWithOutDatabase
     */
    public String getUrlWithOutDatabase() {
        return urlWithOutDatabase;
    }

    /**
     * Setter method for property <tt>urlWithOutDatabase</tt>.
     *
     * @param urlWithOutDatabase value to be assigned to property urlWithOutDatabase
     */
    public void setUrlWithOutDatabase(String urlWithOutDatabase) {
        this.urlWithOutDatabase = urlWithOutDatabase;
    }

    /**
     * Getter method for property <tt>host</tt>.
     *
     * @return property value of host
     */
    public String getHost() {
        return host;
    }

    /**
     * Setter method for property <tt>host</tt>.
     *
     * @param host value to be assigned to property host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Getter method for property <tt>ssh</tt>.
     *
     * @return property value of ssh
     */
    public SSHInfo getSsh() {
        return ssh;
    }

    /**
     * Setter method for property <tt>ssh</tt>.
     *
     * @param ssh value to be assigned to property ssh
     */
    public void setSsh(SSHInfo ssh) {
        this.ssh = ssh;
    }

    /**
     * Getter method for property <tt>ssl</tt>.
     *
     * @return property value of ssl
     */
    public SSLInfo getSsl() {
        return ssl;
    }

    /**
     * Setter method for property <tt>ssl</tt>.
     *
     * @param ssl value to be assigned to property ssl
     */
    public void setSsl(SSLInfo ssl) {
        this.ssl = ssl;
    }

    /**
     * Getter method for property <tt>sid</tt>.
     *
     * @return property value of sid
     */
    public String getSid() {
        return sid;
    }

    /**
     * Setter method for property <tt>sid</tt>.
     *
     * @param sid value to be assigned to property sid
     */
    public void setSid(String sid) {
        this.sid = sid;
    }

    /**
     * Getter method for property <tt>driver</tt>.
     *
     * @return property value of driver
     */
    public String getDriver() {
        return driver;
    }

    /**
     * Setter method for property <tt>driver</tt>.
     *
     * @param driver value to be assigned to property driver
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }

    /**
     * Getter method for property <tt>jdbc</tt>.
     *
     * @return property value of jdbc
     */
    public String getJdbc() {
        return jdbc;
    }

    /**
     * Setter method for property <tt>jdbc</tt>.
     *
     * @param jdbc value to be assigned to property jdbc
     */
    public void setJdbc(String jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Getter method for property <tt>extendInfo</tt>.
     *
     * @return property value of extendInfo
     */
    public List<KeyValue> getExtendInfo() {
        return extendInfo;
    }



    /**
     * Setter method for property <tt>extendInfo</tt>.
     *
     * @param extendInfo value to be assigned to property extendInfo
     */
    public void setExtendInfo(List<KeyValue> extendInfo) {
        this.extendInfo = extendInfo;
    }

    /**
     * Getter method for property <tt>connection</tt>.
     *
     * @return property value of connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Setter method for property <tt>connection</tt>.
     *
     * @param connection value to be assigned to property connection
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }


    /**
     * Getter method for property <tt>alias</tt>.
     *
     * @return property value of alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Setter method for property <tt>alias</tt>.
     *
     * @param alias value to be assigned to property alias
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * Getter method for property <tt>gmtCreate</tt>.
     *
     * @return property value of gmtCreate
     */
    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    /**
     * Setter method for property <tt>gmtCreate</tt>.
     *
     * @param gmtCreate value to be assigned to property gmtCreate
     */
    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }


    /**
     * Getter method for property <tt>gmtModified</tt>.
     *
     * @return property value of gmtModified
     */
    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    /**
     * Setter method for property <tt>gmtModified</tt>.
     *
     * @param gmtModified value to be assigned to property gmtModified
     */
    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

}