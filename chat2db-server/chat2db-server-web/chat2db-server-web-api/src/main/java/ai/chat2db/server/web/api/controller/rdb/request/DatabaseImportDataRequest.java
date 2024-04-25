package ai.chat2db.server.web.api.controller.rdb.request;

import ai.chat2db.server.tools.common.model.data.option.ImportDataOption;
import ai.chat2db.server.tools.common.model.data.option.ImportTableOption;
import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: zgq
 * @date: 2024年04月23日 13:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseImportDataRequest extends DataSourceBaseRequest {
    private ImportTableOption importTableOption;
    private ImportDataOption importDataOption;
}
