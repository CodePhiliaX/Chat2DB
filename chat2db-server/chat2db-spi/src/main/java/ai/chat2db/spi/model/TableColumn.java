package ai.chat2db.spi.model;

import com.fasterxml.jackson.annotation.JsonAlias;
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
public class TableColumn {
    /**
     * 旧的列名，在修改列的时候需要这个参数
     * 在返回的时候oldName=name
     */
    private String oldName;

    /**
     * 列名
     */
    @JsonAlias({"COLUMN_NAME"})
    private String name;

    /**
     * 表名
     */
    @JsonAlias({"TABLE_NAME"})
    private String tableName;

    /**
     * 列的类型
     * 比如 varchar(100) ,double(10,6)
     */

    @JsonAlias({"TYPE_NAME"})
    private String columnType;

    /**
     * 列的数据类型
     * 比如 varchar ,double
     */

    @JsonAlias({"DATA_TYPE"})
    private Integer dataType;


    /**
     * 默认值
     */

    @JsonAlias({"COLUMN_DEF"})
    private String defaultValue;

    /**
     * 是否自增
     * 为空 代表没有值 数据库的实际语义是 false
     */
    private Boolean autoIncrement;

    /**
     * 注释
     */
    @JsonAlias({"REMARKS"})
    private String comment;

    /**
     * 是否主键
     */
    private Boolean primaryKey;

    /**
     * 空间名
     */
    @JsonAlias({"TABLE_SCHEM"})
    private String schemaName;

    /**
     * 数据库名
     */
    @JsonAlias({"TABLE_CAT"})
    private String databaseName;

    /**
     *  Data source dependent type name, for a UDT the type name is fully qualified
     */
    private String typeName;

    /**
     * column size.
     */

    @JsonAlias({"COLUMN_SIZE"})
    private Integer columnSize;

    /**
     * is not used.
     */
    private Integer bufferLength;

    /**
     * the number of fractional digits. Null is returned for data types where DECIMAL_DIGITS is not applicable.
     */

    @JsonAlias({"DECIMAL_DIGITS"})
    private Integer decimalDigits;

    /**
     * Radix (typically either 10 or 2)
     */

    @JsonAlias({"NUM_PREC_RADIX"})
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

    @JsonAlias({"ORDINAL_POSITION"})
    private Integer ordinalPosition;

    /**
     * ISO rules are used to determine the nullability for a column.
     */

    @JsonAlias({"NULLABLE"})
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
