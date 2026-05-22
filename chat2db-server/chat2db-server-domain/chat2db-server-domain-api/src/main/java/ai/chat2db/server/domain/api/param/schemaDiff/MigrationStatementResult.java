package ai.chat2db.server.domain.api.param.schemaDiff;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MigrationStatementResult {
    private int sequence;
    private String sql;
    private boolean success;
    private String errorMessage;
    private Long duration;
}
