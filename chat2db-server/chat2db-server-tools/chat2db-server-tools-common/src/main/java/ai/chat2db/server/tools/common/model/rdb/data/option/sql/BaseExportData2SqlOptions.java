package ai.chat2db.server.tools.common.model.rdb.data.option.sql;

import ai.chat2db.server.tools.common.model.rdb.data.option.BaseExportDataOptions;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: zgq
 * @date: 2024年04月26日 0:36
 */
@Data
@EqualsAndHashCode(callSuper =true)
public class BaseExportData2SqlOptions extends BaseExportDataOptions {
    @NotBlank
    public String sqlType;
    public BaseExportData2SqlOptions() {
        sqlType= "single";
    }
}
