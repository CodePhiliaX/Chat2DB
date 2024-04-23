package ai.chat2db.server.domain.api.param.datasource;

import ai.chat2db.server.tools.common.model.export.data.option.ExportDataOption;
import ai.chat2db.server.tools.common.model.export.data.option.TableOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private ExportDataOption exportDataOption;
    private List<TableOption> tableOptions;
}