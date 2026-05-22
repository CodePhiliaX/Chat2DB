package ai.chat2db.server.domain.api.model.schemaDiff;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SchemaDiffResult {
    private String sourceKey;
    private String targetKey;
    private DiffSummary summary;
    private List<TableDiff> tableDiffs;
    private List<String> warnings;
}
