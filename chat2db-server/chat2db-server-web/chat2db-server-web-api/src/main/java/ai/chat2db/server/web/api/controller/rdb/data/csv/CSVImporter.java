package ai.chat2db.server.web.api.controller.rdb.data.csv;

import ai.chat2db.server.web.api.controller.rdb.data.DataFileImporter;
import ai.chat2db.server.web.api.controller.rdb.data.xlsx.BaseExcelImporter;
import com.alibaba.excel.support.ExcelTypeEnum;

/**
 * 功能描述
 *
 * @author: zgq
 * @date: 2024年04月26日 11:26
 */
public class CSVImporter extends BaseExcelImporter implements DataFileImporter {


    @Override
    protected ExcelTypeEnum getExcelType() {
        return ExcelTypeEnum.CSV;
    }
}
