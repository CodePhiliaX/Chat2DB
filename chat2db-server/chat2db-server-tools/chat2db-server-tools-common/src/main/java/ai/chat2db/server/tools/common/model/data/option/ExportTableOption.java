package ai.chat2db.server.tools.common.model.data.option;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExportTableOption {
    @NotNull
    private String tableName;
    @NotEmpty
    @NotNull
    private List<String> exportColumnNames;

}