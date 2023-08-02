package ai.chat2db.spi.util;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

import com.alibaba.druid.DbType;

import ai.chat2db.spi.config.DriverConfig;
import ai.chat2db.spi.enums.DataTypeEnum;
import ai.chat2db.spi.model.DataSourceConnect;
import ai.chat2db.spi.model.SSHInfo;
import ai.chat2db.spi.sql.IDriverManager;
import ai.chat2db.spi.ssh.SSHManager;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * jdbc工具类
 *
 * @author Jiaju Zhuang
 */
@Slf4j
public class JdbcUtils {

    /**
     * 获取德鲁伊的的数据库类型
     *
     * @param dbType
     * @return
     */
    public static DbType parse2DruidDbType(String dbType) {
        if (dbType == null) {
            return null;
        }
        try {
            return DbType.valueOf(dbType.toLowerCase());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解析字段的类型
     *
     * @param typeName
     * @param type
     * @return
     */
    public static DataTypeEnum resolveDataType(String typeName, int type) {
        switch (getTypeByTypeName(typeName, type)) {
            case Types.BOOLEAN:
                return DataTypeEnum.BOOLEAN;
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.NVARCHAR:
            case Types.LONGVARCHAR:
            case Types.LONGNVARCHAR:
                return DataTypeEnum.STRING;
            case Types.BIGINT:
            case Types.DECIMAL:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.INTEGER:
            case Types.NUMERIC:
            case Types.REAL:
            case Types.SMALLINT:
                return DataTypeEnum.NUMERIC;
            case Types.BIT:
            case Types.TINYINT:
                if (typeName.toLowerCase().contains("bool")) {
                    // Declared as numeric but actually it's a boolean
                    return DataTypeEnum.BOOLEAN;
                }
                return DataTypeEnum.NUMERIC;
            case Types.DATE:
            case Types.TIME:
            case Types.TIME_WITH_TIMEZONE:
            case Types.TIMESTAMP:
            case Types.TIMESTAMP_WITH_TIMEZONE:
                return DataTypeEnum.DATETIME;
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
                return DataTypeEnum.BINARY;
            case Types.BLOB:
            case Types.CLOB:
            case Types.NCLOB:
            case Types.SQLXML:
                return DataTypeEnum.CONTENT;
            case Types.STRUCT:
                return DataTypeEnum.STRUCT;
            case Types.ARRAY:
                return DataTypeEnum.ARRAY;
            case Types.ROWID:
                return DataTypeEnum.ROWID;
            case Types.REF:
                return DataTypeEnum.REFERENCE;
            case Types.OTHER:
                return DataTypeEnum.OBJECT;
            default:
                return DataTypeEnum.UNKNOWN;
        }
    }

    private static int getTypeByTypeName(String typeName, int type) {
        // [JDBC: SQLite driver uses VARCHAR value type for all LOBs]
        if (type == Types.OTHER || type == Types.VARCHAR) {
            if ("BLOB".equalsIgnoreCase(typeName)) {
                return Types.BLOB;
            } else if ("CLOB".equalsIgnoreCase(typeName)) {
                return Types.CLOB;
            } else if ("NCLOB".equalsIgnoreCase(typeName)) {
                return Types.NCLOB;
            }
        } else if (type == Types.BIT) {
            // Workaround for MySQL (and maybe others) when TINYINT(1) == BOOLEAN
            if ("TINYINT".equalsIgnoreCase(typeName)) {
                return Types.TINYINT;
            }
        }
        return type;
    }

    /**
     * 获取一个返回值
     *
     * @param rs
     * @param index
     * @return
     * @throws SQLException
     */
    public static String getResultSetValue(ResultSet rs, int index) throws SQLException {
        Object obj = rs.getObject(index);
        if (obj == null) {
            return null;
        }

        if (obj instanceof Blob blob) {
            return "(BLOB " + blob.length() + ")";
        }
        if (obj instanceof Clob clob) {
            return "(CLOB " + clob.length() + ")";
        }
        if (obj instanceof Timestamp timestamp) {
            return Objects.toString(timestamp);
        }

        String className = obj.getClass().getName();
        if ("oracle.sql.TIMESTAMP".equals(className) || "oracle.sql.TIMESTAMPTZ".equals(className)) {
            return Objects.toString(rs.getTimestamp(index));
        }
        if (className.startsWith("oracle.sql.DATE")) {
            String metaDataClassName = rs.getMetaData().getColumnClassName(index);
            if ("java.sql.Timestamp".equals(metaDataClassName) || "oracle.sql.TIMESTAMP".equals(metaDataClassName)) {
                return Objects.toString(rs.getTimestamp(index));
            } else {
                return Objects.toString(rs.getDate(index));
            }
        }
        if (obj instanceof Date date) {
            if ("java.sql.Timestamp".equals(rs.getMetaData().getColumnClassName(index))) {
                return Objects.toString(rs.getDate(index));
            }
            return Objects.toString(date);
        }
        if (obj instanceof LocalDateTime localDateTime) {
            return Objects.toString(localDateTime);
        }
        if (obj instanceof LocalDate localDate) {
            return Objects.toString(localDate);
        }
        if (obj instanceof BigDecimal bigDecimal) {
            return bigDecimal.toPlainString();
        }
        if (obj instanceof Double d) {
            return BigDecimal.valueOf(d).toPlainString();
        }
        if (obj instanceof Float f) {
            return BigDecimal.valueOf(f).toPlainString();
        }
        if (obj instanceof Number num) {
            return Objects.toString(num);
        }
        return Objects.toString(obj);
    }

    /**
     * 测试数据库连接
     *
     * @param url      数据库连接
     * @param userName 用户名
     * @param password 密码
     * @param dbType   数据库类型
     * @return
     */
    public static DataSourceConnect testConnect(String url, String host, String port,
        String userName, String password, String dbType,
        DriverConfig driverConfig, SSHInfo ssh, Map<String, Object> properties) {
        DataSourceConnect dataSourceConnect = DataSourceConnect.builder()
            .success(Boolean.TRUE)
            .build();
        Session session = null;
        Connection connection = null;
        // 加载驱动
        try {
            if (ssh.isUse()) {
                ssh.setRHost(host);
                ssh.setRPort(port);
                session = SSHManager.getSSHSession(ssh);
                url = url.replace(host, "127.0.0.1").replace(port, ssh.getLocalPort());
            }
            // 创建连接
            connection = IDriverManager.getConnection(url, userName, password,
                driverConfig, properties);
        } catch (Exception e) {
            log.error("connection fail:", e);
            dataSourceConnect.setSuccess(Boolean.FALSE);
            // 获取最后一个异常的信息给前端
            Throwable t = e;
            while (t.getCause() != null) {
                t = t.getCause();
            }
            dataSourceConnect.setMessage(t.getMessage());
            dataSourceConnect.setErrorDetail(ExceptionUtils.getErrorInfoFromException(t));
            return dataSourceConnect;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
            if (session != null) {
                try {
                    if (StringUtils.isNotBlank(ssh.getLocalPort())) {
                        session.delPortForwardingL(Integer.parseInt(ssh.getLocalPort()));
                    }
                    session.disconnect();
                } catch (Exception e) {

                }
            }
        }
        dataSourceConnect.setDescription("成功");
        return dataSourceConnect;
    }

}
