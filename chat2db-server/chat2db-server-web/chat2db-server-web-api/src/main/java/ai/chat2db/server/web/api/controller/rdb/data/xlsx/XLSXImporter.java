package ai.chat2db.server.web.api.controller.rdb.data.xlsx;

import ai.chat2db.server.web.api.controller.rdb.data.DataFileImporter;
import com.alibaba.excel.support.ExcelTypeEnum;

/**
 * @author: zgq
 * @date: 2024年04月26日 14:19
 */
public class XLSXImporter extends BaseExcelImporter implements DataFileImporter {

    @Override
    protected ExcelTypeEnum getExcelType() {
        return ExcelTypeEnum.XLSX;
    }
}
