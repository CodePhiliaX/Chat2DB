package ai.chat2db.spi.model;

import lombok.Data;

import java.util.List;

/**
 * constraint info
 *
 * @author: zgq
 * @date: 2024年05月16日 10:19
 */
@Data
public class TableConstraint {

    private String databaseName;

    private String schemaName;

    private String tableName;

    private String constraintName;

    /**
     * @see ai.chat2db.spi.enums.ConstraintTypeEnum
     */
    private String constraintType;

    private String constraintDefinition;

    private List<TableConstraintColumn> constraintColumnList;

    private String referenceTableName;

    private List<String> referenceColumnNames;


    /**
     * sqlserver foreign key delete cascade
     */
    private Integer deleteAction;

    /**
     * sqlserver foreign key update cascade
     */
    private Integer updateAction;


    /**
     * CLUSTERED or NONCLUSTERED
     */
    private String ClusteredOrNonclustered;

    private String comment;
}
