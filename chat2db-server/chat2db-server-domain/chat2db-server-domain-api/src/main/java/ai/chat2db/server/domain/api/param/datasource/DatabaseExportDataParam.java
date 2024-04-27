package ai.chat2db.server.domain.api.param.datasource;

import ai.chat2db.server.tools.common.model.data.option.AbstractExportDataOptions;
import ai.chat2db.server.tools.common.model.data.option.table.BaseTableOptions;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    private AbstractExportDataOptions exportDataOption;
    private List<BaseTableOptions> exportTableOptions;
}