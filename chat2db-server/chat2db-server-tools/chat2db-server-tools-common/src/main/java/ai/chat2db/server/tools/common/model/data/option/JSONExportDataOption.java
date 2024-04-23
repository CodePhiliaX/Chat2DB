package ai.chat2db.server.tools.common.model.data.option;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: zgq
 * @date: 2024年04月22日 18:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JSONExportDataOption extends ExportDataOption {

    @NotBlank
    private String dataTimeFormat;
    @NotNull
    private Boolean isTimestamps;

}
