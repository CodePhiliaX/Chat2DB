package ai.chat2db.server.admin.api.controller.datasource.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * create
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceAccessBatchCreateRequest {

    /**
     * 数据源id
     */
    @NotNull
    private Long dataSourceId;

    /**
     * DataSource Access Object
     */
    @NotNull
    private List<DataSourceAccessObjectRequest> accessObjectList;
}
