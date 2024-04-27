package ai.chat2db.server.tools.common.model.data.option;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 功能描述
 *
 * @author: zgq
 * @date: 2024年04月26日 0:02
 */
@Data
@EqualsAndHashCode(callSuper =true)
public class BaseExportDataOptions extends AbstractExportDataOptions {
    @NotNull
    public Boolean containsHeader;

    public BaseExportDataOptions() {
        this.containsHeader = true;
    }
}
