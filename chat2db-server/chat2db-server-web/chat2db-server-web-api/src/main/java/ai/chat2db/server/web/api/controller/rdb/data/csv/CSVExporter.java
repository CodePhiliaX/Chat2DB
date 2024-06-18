package ai.chat2db.server.web.api.controller.rdb.data.csv;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.web.api.controller.rdb.data.DataFileExporter;
import ai.chat2db.server.web.api.controller.rdb.data.xlsx.BaseExcelExporter;
import com.alibaba.excel.support.ExcelTypeEnum;

/**
 * @author: zgq
 * @date: 2024年04月26日 11:27
 */
public class CSVExporter extends BaseExcelExporter implements DataFileExporter {


    public CSVExporter() {
        suffix = ExportFileSuffix.CSV.getSuffix();
        contentType = "text/csv";
    }
    @Override
    protected ExcelTypeEnum getExcelType() {
        return ExcelTypeEnum.CSV;
    }
}
