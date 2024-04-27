package ai.chat2db.server.tools.common.model.rdb.data.option;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: zgq
 * @date: 2024年04月26日 10:06
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseImportExcelDataOptions extends BaseImportDataOptions {
    public Integer headerRowNum;

    public BaseImportExcelDataOptions() {
        this.headerRowNum = 1;
    }
}
