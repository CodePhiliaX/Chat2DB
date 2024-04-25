package ai.chat2db.server.tools.common.model.data.option;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: zgq
 * @date: 2024年04月24日 16:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JSONImportDataOption extends ImportDataOption{
    private String rootNodeName;
}
