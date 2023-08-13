
package ai.chat2db.spi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jipengfei
 * @version : Function.java
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Function {
    //FUNCTION_CAT String => function catalog (may be null)
    //FUNCTION_SCHEM String => function schema (may be null)
    //FUNCTION_NAME String => function name. This is the name used to invoke the function
    //REMARKS String => explanatory comment on the function
    //FUNCTION_TYPE short => kind of function:
    //functionResultUnknown - Cannot determine if a return value or table will be returned
    //functionNoTable- Does not return a table
    //functionReturnsTable - Returns a table
    //SPECIFIC_NAME String => the name which uniquely identifies this function within its schema. This is a user specified, or DBMS generated, name that may be different then the FUNCTION_NAME for example with overload functions
    //

    private String databaseName;

    private String schemaName;

    private String functionName;

    private String remarks;

    private Short functionType;

    private String specificName;

    private String functionBody;

}