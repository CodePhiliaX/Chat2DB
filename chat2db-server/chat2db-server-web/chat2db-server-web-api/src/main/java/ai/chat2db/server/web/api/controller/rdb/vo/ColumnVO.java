package ai.chat2db.server.web.api.controller.rdb.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author moji
 * @version TableVO.java, v 0.1 September 16, 2022 17:16 moji Exp $
 * @date 2022/09/16
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ColumnVO {
    /**
     * The old column name, this parameter is needed when modifying the column
     * oldName=name when returning
     */
    private String oldName;

    /**
     * Column name
     */
    private String name;

    /**
     * Table Name
     */
    private String tableName;

    /**
     * Column type
     * For example, varchar(100), double(10,6)
     */

    private String columnType;

    /**
     * Column data type
     * For example, varchar, double
     */

    private Integer dataType;


    /**
     * default value
     */

    private String defaultValue;

    /**
     * Whether to increase automatically
     * Empty means there is no value. The actual semantics of the database are false.
     */
    private Boolean autoIncrement;

    /**
     * Comment
     */
    private String comment;

    /**
     * Is it a primary key?
     */
    private Boolean primaryKey;

    /**
     * Space name
     */
    private String schemaName;

    /**
     * Database name
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
