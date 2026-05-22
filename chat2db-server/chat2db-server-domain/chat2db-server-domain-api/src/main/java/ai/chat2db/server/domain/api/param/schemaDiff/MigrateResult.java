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
public class MigrateResult {
    private boolean success;
    private List<MigrationStatementResult> statementResults;
    private int totalStatements;
    private int successCount;
    private int failCount;
}
