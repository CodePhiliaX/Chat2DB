package ai.chat2db.server.test.domain.data.service.dialect;

import java.util.Date;



/**
 * 方言配置
 *
 * @author Jiaju Zhuang
 */
public interface DialectProperties {

    /**
     * 支持的数据库类型
     *
     * @return
     */
    String getDbType();

    /**
     * 连接
     *
     * @return
     */
    String getUrl();

    /**
     * 异常连接
     *
     * @return
     */
    String getErrorUrl();

    /**
     * 用户名
     *
     * @return
     */

    String getUsername();

    /**
     * 密码
     *
     * @return
     */
    String getPassword();

    /**
     * 数据库名称
     *
     * @return
     */
    String getDatabaseName();

    /**
     * 大小写看具体的数据库决定：
     * 创建表表结构 : 测试表
     * 字段：
     * id   主键自增
     * date 日期 非空
     * number 长整型
     * string  字符串 长度100 默认值 "DATA"
     *
     * 索引(加上$tableName_ 原因是 有些数据库索引是全局唯一的)：
     * $tableName_idx_date 日期索引 倒序
     * $tableName_uk_number 唯一索引
     * $tableName_idx_number_string 联合索引
     *
     * @return
     */
    String getCrateTableSql(String tableName);

    /**
     * 创建表表结构
     *
     * @return
     */
    String getDropTableSql(String tableName);

    /**
     * 创建一条数据
     *
     * @return
     */
    String getInsertSql(String tableName, Date date, Long number, String string);

    /**
     * 查询一条查询sql
     *
     * @return
     */
    String getSelectSqlById(String tableName, Long id);

    /**
     * 获取一条表结构不存在的sql
     *
     * @return
     */
    String getTableNotFoundSqlById(String tableName);

    /**
     * 转换大小写
     * 有些数据库表结构默认存储大写
     * 有些数据库默认存储小写
     *
     * @param string
     * @return
     */
    String toCase(String string);
}
