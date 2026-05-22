package ai.chat2db.spi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 列信息
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TableColumn implements IndexModel {

    /**
     * Old column, when modifying a column, you need this parameter
     */
    private TableColumn oldColumn;
    /**
     * 旧的列名，在修改列的时候需要这个参数
     * 在返回的时候oldName=name
     */
    private String oldName;

    /**
     * 列名
     */
    @LuceneField(name = "name", type = LuceneFieldType.TEXT, sort = true)
    private String name;

    /**
     * 表名
     */
    @LuceneField(name = "tableName", type = LuceneFieldType.STRING)
    private String tableName;

    /**
     * 列的类型
     * 比如 varchar(100) ,double(10,6)
     */
    @LuceneField(name = "columnType", type = LuceneFieldType.TEXT)
    private String columnType;

    /**
     * 列的数据类型
     * 比如 varchar ,double
     */
    private String dataType;


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
    @LuceneField(name = "comment", type = LuceneFieldType.TEXT)
    private String comment;

    /**
     * 是否主键
     */
    private Boolean primaryKey;


    /**
     * 主键名
     */
    private String primaryKeyName;


    /**
     * 主键顺序
     */
    private int primaryKeyOrder;

    /**
     * 空间名
     */
    @LuceneField(name = "schemaName", type = LuceneFieldType.STRING)
    private String schemaName;

    /**
     * 数据库名
     */
    @LuceneField(name = "databaseName", type = LuceneFieldType.STRING)
    private String databaseName;

//    /**
//     * Data source dependent type name, for a UDT the type name is fully qualified
//     */
//    private String typeName;

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
     * * YES --- if this a generated column
     * * NO --- if this not a generated column
     */
    private Boolean generatedColumn;


    private String extent;


    private String editStatus;

    private String charSetName;

    private String collationName;

    //Mysql
    private String value;

    //ORACLE
    private String unit;

    // sqlserver
    private Boolean sparse;

    // sqlserver
    private String defaultConstraintName;

    /**
     * AI生成的注释
     */
    @LuceneField(name = "aiComment", type = LuceneFieldType.TEXT)
    private String aiComment;

    /**
     * 版本
     */
    private Long version;

}
