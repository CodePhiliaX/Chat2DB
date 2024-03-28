package ai.chat2db.server.web.api.controller.rdb.request;

import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: zgq
 * @date: 2024年03月24日 12:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseExportDataRequest extends DataSourceBaseRequest {
    @NotNull
    private String exportType;
}