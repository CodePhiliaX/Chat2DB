package ai.chat2db.server.web.api.controller.rdb.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: zgq
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableExportDataRequest extends DatabaseExportDataRequest {
    @NotBlank
    private String tableName;
}
