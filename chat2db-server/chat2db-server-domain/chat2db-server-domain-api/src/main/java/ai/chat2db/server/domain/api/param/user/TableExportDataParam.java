package ai.chat2db.server.domain.api.param.user;

import ai.chat2db.server.domain.api.param.datasource.DatabaseExportDataParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: zgq
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableExportDataParam extends DatabaseExportDataParam {
    private String tableName;
}
