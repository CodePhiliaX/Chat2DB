package ai.chat2db.server.domain.api.model.schemaDiff;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DiffSummary {
    private int totalTables;
    private int tablesOnlyInSource;
    private int tablesOnlyInTarget;
    private int modifiedTables;
    private int unchangedTables;
    private int excludedDeprecatedTables;
}
