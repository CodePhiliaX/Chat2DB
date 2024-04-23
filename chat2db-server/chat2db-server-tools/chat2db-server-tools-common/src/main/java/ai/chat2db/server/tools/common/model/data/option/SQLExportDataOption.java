package ai.chat2db.server.tools.common.model.data.option;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: zgq
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SQLExportDataOption extends ExportDataOption {
    private Boolean multipleRow;
    private String newTableName;
    private Boolean toUpdate;
}
