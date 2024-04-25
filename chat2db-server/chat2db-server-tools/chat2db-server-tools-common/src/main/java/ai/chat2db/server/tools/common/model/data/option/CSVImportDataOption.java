package ai.chat2db.server.tools.common.model.data.option;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: zgq
 * @date: 2024年04月24日 20:49
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CSVImportDataOption extends ImportDataOption {
    private Integer headerRowNum;
    private Integer dataStartRowNum;
    private Integer dataEndRowNum;
}
