package ai.chat2db.server.tools.common.model.rdb.data.option;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: zgq
 * @date: 2024年04月25日 23:40
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseImportDataOptions extends AbstractImportDataOptions {
    public Integer dataStartRowNum;
    public Integer dataEndRowNum;

    public BaseImportDataOptions() {
        dataStartRowNum = 1;
        dataEndRowNum = Integer.MAX_VALUE;
    }
}
