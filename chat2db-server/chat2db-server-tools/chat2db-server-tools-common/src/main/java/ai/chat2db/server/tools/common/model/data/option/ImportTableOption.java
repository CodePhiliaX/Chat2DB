package ai.chat2db.server.tools.common.model.data.option;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: zgq
 * @date: 2024年04月24日 16:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportTableOption {
    private String tableName;
    private List<String> srcColumnNames;
    private List<String> targetColumnNames;
}
