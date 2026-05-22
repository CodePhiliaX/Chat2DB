package ai.chat2db.server.domain.api.param.schemaDiff;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SchemaMigrateParam {
    private Long targetDataSourceId;
    private String targetDatabaseName;
    private String targetSchemaName;
    private List<String> ddlStatements;
    private boolean executeInTransaction;
    private boolean continueOnError;
}
