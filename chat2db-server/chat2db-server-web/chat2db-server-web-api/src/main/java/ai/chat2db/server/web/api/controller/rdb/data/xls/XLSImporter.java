package ai.chat2db.server.web.api.controller.rdb.data.xls;

import ai.chat2db.server.web.api.controller.rdb.data.DataFileImporter;
import ai.chat2db.server.web.api.controller.rdb.data.xlsx.BaseExcelImporter;
import com.alibaba.excel.support.ExcelTypeEnum;

/**
 * @author: zgq
 * @date: 2024年04月29日 21:48
 */
public class XLSImporter extends BaseExcelImporter implements DataFileImporter {

    @Override
    protected ExcelTypeEnum getExcelType() {
        return ExcelTypeEnum.XLS;
    }
}
