package ai.chat2db.server.domain.api.param.datasource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: zgq
 * @date: 2024年03月24日 13:17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseExportDataParam {
    private String databaseName;
    private String schemaName;
    private String exportType;
}