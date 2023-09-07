package ai.chat2db.spi.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Type {

    @JsonAlias("TYPE_NAME")
    private String typeName;

    @JsonAlias("DATA_TYPE")
    private Integer dataType;

    @JsonAlias("PRECISION")
    private Integer precision;

    @JsonAlias("LITERAL_PREFIX")
    private String literalPrefix;

    @JsonAlias("LITERAL_SUFFIX")
    private String literalSuffix;

    @JsonAlias("CREATE_PARAMS")
    private String createParams;

    @JsonAlias("NULLABLE")
    private Short nullable;


    @JsonAlias("CASE_SENSITIVE")
    private Boolean caseSensitive;

    @JsonAlias("SEARCHABLE")
    private Short searchable;

    @JsonAlias("UNSIGNED_ATTRIBUTE")
    private Boolean unsignedAttribute;


    @JsonAlias("FIXED_PREC_SCALE")
    private Boolean fixedPrecScale;

    @JsonAlias("AUTO_INCREMENT")
    private Boolean autoIncrement;

    @JsonAlias("LOCAL_TYPE_NAME")
    private String localTypeName;

    @JsonAlias("MINIMUM_SCALE")
    private Short minimumScale;

    @JsonAlias("MAXIMUM_SCALE")
    private Short maximumScale;

    @JsonAlias("SQL_DATA_TYPE")
    private Integer sqlDataType;

    @JsonAlias("SQL_DATETIME_SUB")
    private Integer sqlDatetimeSub;


    @JsonAlias("NUM_PREC_RADIX")
    private Integer numPrecRadix;



//    TYPE_NAME String => Type name
//    DATA_TYPE int => SQL data type from java.sql.Types
//    PRECISION int => maximum precision
//    LITERAL_PREFIX String => prefix used to quote a literal (may be null)
//    LITERAL_SUFFIX String => suffix used to quote a literal (may be null)
//    CREATE_PARAMS String => parameters used in creating the type (may be null)
//    NULLABLE short => can you use NULL for this type.
//            typeNoNulls - does not allow NULL values
//    typeNullable - allows NULL values
//    typeNullableUnknown - nullability unknown
//    CASE_SENSITIVE boolean=> is it case sensitive.
//            SEARCHABLE short => can you use "WHERE" based on this type:
//    typePredNone - No support
//    typePredChar - Only supported with WHERE .. LIKE
//    typePredBasic - Supported except for WHERE .. LIKE
//    typeSearchable - Supported for all WHERE ..
//    UNSIGNED_ATTRIBUTE boolean => is it unsigned.
//            FIXED_PREC_SCALE boolean => can it be a money value.
//    AUTO_INCREMENT boolean => can it be used for an auto-increment value.
//    LOCAL_TYPE_NAME String => localized version of type name (may be null)
//    MINIMUM_SCALE short => minimum scale supported
//    MAXIMUM_SCALE short => maximum scale supported
//    SQL_DATA_TYPE int => unused
//    SQL_DATETIME_SUB int => unused
//    NUM_PREC_RADIX int => usually 2 or 10
}
