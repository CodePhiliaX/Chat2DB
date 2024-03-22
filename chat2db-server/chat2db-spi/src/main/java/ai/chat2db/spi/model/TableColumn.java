package ai.chat2db.spi.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

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
    @JsonAlias({"COLUMN_NAME","column_name"})
    private String name;

    /**
     * 表名
     */
    @JsonAlias({"TABLE_NAME","table_name"})
    private String tableName;

    /**
     * 列的类型
     * 比如 varchar(100) ,double(10,6)
     */

    @JsonAlias({"TYPE_NAME","type_name"})
    private String columnType;

    /**
     * 列的数据类型
     * 比如 varchar ,double
     */

    @JsonAlias({"DATA_TYPE","data_type"})
    private Integer dataType;


    /**
     * 默认值
     */

    @JsonAlias({"COLUMN_DEF","column_def"})
    private String defaultValue;



    /**
     * 是否自增
     * 为空 代表没有值 数据库的实际语义是 false
     */
    private Boolean autoIncrement;

    /**
     * 注释
     */
    @JsonAlias({"REMARKS","remarks"})
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
    @JsonAlias({"TABLE_SCHEM","table_schem"})
    private String schemaName;

    /**
     * 数据库名
     */
    @JsonAlias({"TABLE_CAT","table_cat"})
    private String databaseName;

//    /**
//     * Data source dependent type name, for a UDT the type name is fully qualified
//     */
//    private String typeName;

    /**
     * column size.
     */

    @JsonAlias({"COLUMN_SIZE","column_size"})
    private Integer columnSize;

    /**
     * is not used.
     */
    private Integer bufferLength;

    /**
     * the number of fractional digits. Null is returned for data types where DECIMAL_DIGITS is not applicable.
     */

    @JsonAlias({"DECIMAL_DIGITS","decimal_digits"})
    private Integer decimalDigits;

    /**
     * Radix (typically either 10 or 2)
     */

    @JsonAlias({"NUM_PREC_RADIX","num_prec_radix"})
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

    @JsonAlias({"ORDINAL_POSITION","ordinal_position"})
    private Integer ordinalPosition;

    /**
     * ISO rules are used to determine the nullability for a column.
     */

    @JsonAlias({"NULLABLE","nullable"})
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableColumn that = (TableColumn) o;
        return Objects.equals(name, that.name) && Objects.equals(tableName, that.tableName) && Objects.equals(columnType, that.columnType) && Objects.equals(defaultValue, that.defaultValue) && Objects.equals(autoIncrement, that.autoIncrement) && Objects.equals(comment, that.comment) && Objects.equals(columnSize, that.columnSize) && Objects.equals(decimalDigits, that.decimalDigits) && Objects.equals(numPrecRadix, that.numPrecRadix) && Objects.equals(sqlDataType, that.sqlDataType) && Objects.equals(ordinalPosition, that.ordinalPosition) && Objects.equals(nullable, that.nullable) && Objects.equals(extent, that.extent) && Objects.equals(charSetName, that.charSetName) && Objects.equals(collationName, that.collationName) && Objects.equals(value, that.value) && Objects.equals(unit, that.unit) && Objects.equals(sparse, that.sparse) && Objects.equals(defaultConstraintName, that.defaultConstraintName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, tableName, columnType, defaultValue, autoIncrement, comment, columnSize, decimalDigits, numPrecRadix, sqlDataType, ordinalPosition, nullable, extent, charSetName, collationName, value, unit, sparse, defaultConstraintName);
    }
}
