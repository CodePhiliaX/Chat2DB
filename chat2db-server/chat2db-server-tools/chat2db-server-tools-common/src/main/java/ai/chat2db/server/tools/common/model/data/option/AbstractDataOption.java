package ai.chat2db.server.tools.common.model.data.option;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author: zgq
 * @date: 2024年04月25日 22:59
 */
@Data
public abstract class AbstractDataOption implements AbstractDataOptionInterface {
    @NotNull
    protected String fileType;

}
