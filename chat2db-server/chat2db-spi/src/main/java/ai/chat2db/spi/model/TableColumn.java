package ai.chat2db.spi.model;

import com.alibaba.fastjson2.annotation.JSONField;

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
    @JSONField(alternateNames = {"COLUMN_NAME", "column_name"})
    @LuceneField(name = "name", type = LuceneFieldType.TEXT, sort = true)
    private String name;

    /**
     * 表名
     */
    @JSONField(alternateNames = {"TABLE_NAME", "table_name"})
    @LuceneField(name = "tableName", type = LuceneFieldType.STRING)
    private String tableName;

    /**
     * 列的类型
     * 比如 varchar(100) ,double(10,6)
     */
    @JSONField(alternateNames = {"TYPE_NAME", "type_name"})
    @LuceneField(name = "columnType", type = LuceneFieldType.TEXT)
    private String columnType;

    /**
     * 列的数据类型
     * 比如 varchar ,double
     */
    @JSONField(alternateNames = {"DATA_TYPE", "data_type"})
    private String dataType;


    /**
     * 默认值
     */
    @JSONField(alternateNames = {"COLUMN_DEF", "column_def"})
    private String defaultValue;


    /**
     * 是否自增
     * 为空 代表没有值 数据库的实际语义是 false
     */
    @JSONField(alternateNames = {"IS_AUTOINCREMENT"})
    private Boolean autoIncrement;

    /**
     * 注释
     */
    @JSONField(alternateNames = {"REMARKS", "remarks"})
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
    @JSONField(alternateNames = {"KEY_SEQ"})
    private int primaryKeyOrder;

    /**
     * 空间名
     */
    @JSONField(alternateNames = {"TABLE_SCHEM", "table_schem"})
    @LuceneField(name = "schemaName", type = LuceneFieldType.STRING)
    private String schemaName;

    /**
     * 数据库名
     */
    @JSONField(alternateNames = {"TABLE_CAT", "table_cat"})
    @LuceneField(name = "databaseName", type = LuceneFieldType.STRING)
    private String databaseName;

//    /**
//     * Data source dependent type name, for a UDT the type name is fully qualified
//     */
//    private String typeName;

    /**
     * column size.
     */

    @JSONField(alternateNames = {"COLUMN_SIZE", "column_size"})
    private Integer columnSize;

    /**
     * is not used.
     */
    private Integer bufferLength;

    /**
     * the number of fractional digits. Null is returned for data types where DECIMAL_DIGITS is not applicable.
     */

    @JSONField(alternateNames = {"DECIMAL_DIGITS", "decimal_digits"})
    private Integer decimalDigits;

    /**
     * Radix (typically either 10 or 2)
     */

    @JSONField(alternateNames = {"NUM_PREC_RADIX", "num_prec_radix"})
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

    @JSONField(alternateNames = {"ORDINAL_POSITION", "ordinal_position"})
    private Integer ordinalPosition;

    /**
     * ISO rules are used to determine the nullability for a column.
     */
    @JSONField(alternateNames = {"NULLABLE", "nullable"})
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
