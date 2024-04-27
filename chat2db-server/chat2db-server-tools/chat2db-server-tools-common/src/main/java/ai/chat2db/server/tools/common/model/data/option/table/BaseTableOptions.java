package ai.chat2db.server.tools.common.model.data.option.table;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * @author: zgq
 * @date: 2024年04月25日 23:53
 */
@Data
public class BaseTableOptions implements TableOptionInterface {
    @NotBlank
    public String tableName;
    @NotEmpty
    public List<String> tableColumns;
    @NotEmpty
    public List<String> fileColumns;
}
