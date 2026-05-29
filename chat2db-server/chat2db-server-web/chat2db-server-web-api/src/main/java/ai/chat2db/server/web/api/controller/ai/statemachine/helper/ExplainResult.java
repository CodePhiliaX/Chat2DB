package ai.chat2db.server.web.api.controller.ai.statemachine.helper;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExplainResult {
    private boolean success;
    private String explainSql;
    private String formattedPlan;
    private List<List<String>> planRows;
    private String errorMessage;
}
