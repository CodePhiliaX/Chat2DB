package ai.chat2db.server.web.api.controller.rdb.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author moji
 * @version TableVO.java, v 0.1 2022年09月16日 17:16 moji Exp $
 * @date 2022/09/16
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ColumnVO {
    /**
     * 旧的列名，在修改列的时候需要这个参数
     * 在返回的时候oldName=name
     */
    private String oldName;

    /**
     * 列名
     */
    private String name;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 列的类型
     * 比如 varchar(100) ,double(10,6)
     */

    private String columnType;

    /**
     * 列的数据类型
     * 比如 varchar ,double
     */

    private Integer dataType;


    /**
     * 默认值
     */

    private String defaultValue;

    /**
     * 是否自增
     * 为空 代表没有值 数据库的实际语义是 false
     */
    private Boolean autoIncrement;

    /**
     * 注释
     */
    private String comment;

    /**
     * 是否主键
     */
    private Boolean primaryKey;

    /**
     * 空间名
     */
    private String schemaName;

    /**
     * 数据库名
     */
    private String databaseName;

    /**
     *  Data source dependent type name, for a UDT the type name is fully qualified
     */
    private String typeName;

    /**
     * column size.
     */

    private Integer columnSize;

    /**
     * is not used.
     */
    private Integer bufferLength;

    /**
     * the number of fractional digits. Null is returned for data types where DECIMAL_DIGITS is not applicable.
     */

    private Integer decimalDigits;

    /**
     * Radix (typically either 10 or 2)
     */

    private Integer numPrecRadix;

    /**
     * is NULL allowed.
     * columnNoNulls - might not allow NULL values
     * columnNullable - definitely allows NULL values
     * columnNullableUnknown - nullability unknown
     */
    private Integer nullableInt;

    /**
     * unused
     */
    private Integer sqlDataType;


    /**
     * unused
     */
    private Integer sqlDatetimeSub;

    /**
     * for char types the maximum number of bytes in the column
     */
    private Integer charOctetLength;

    /**
     * index of column in table (starting at 1)
     */

    private Integer ordinalPosition;

    /**
     * ISO rules are used to determine the nullability for a column.
     */

    private Integer nullable;

    /**
     * String => Indicates whether this is a generated column
     *      * YES --- if this a generated column
     *      * NO --- if this not a generated column
     */
    private Boolean generatedColumn;


    private String extent;


    private String editStatus;

}
