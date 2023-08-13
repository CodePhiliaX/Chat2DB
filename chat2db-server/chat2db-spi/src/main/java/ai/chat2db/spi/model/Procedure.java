
package ai.chat2db.spi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jipengfei
 * @version : Procedure.java
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Procedure {
    //PROCEDURE_CAT String => procedure catalog (may be null)
    //PROCEDURE_SCHEM String => procedure schema (may be null)
    //PROCEDURE_NAME String => procedure name
    //REMARKS String => explanatory comment on the procedure
    //PROCEDURE_TYPE short => kind of procedure:
    //procedureResultUnknown - Cannot determine if a return value will be returned
    //procedureNoResult - Does not return a return value
    //procedureReturnsResult - Returns a return value
    //SPECIFIC_NAME String => the name which uniquely identifies this procedure within its schema. This is a user specified, or DBMS generated, name that may be different then the PROCEDURE_NAME for example with overload procedures
    //

    private String databaseName;

    private String schemaName;

    private String procedureName;

    private String remarks;

    private Short procedureType;

    private String specificName;

    private String procedureBody;
}