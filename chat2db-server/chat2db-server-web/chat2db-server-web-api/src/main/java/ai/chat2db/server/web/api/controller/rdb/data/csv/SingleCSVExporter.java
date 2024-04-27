package ai.chat2db.server.web.api.controller.rdb.data.csv;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.web.api.controller.rdb.data.BaseSingleExcelExporter;
import ai.chat2db.server.web.api.controller.rdb.data.SingleFileExporter;
import com.alibaba.excel.support.ExcelTypeEnum;

/**
 * 功能描述
 *
 * @author: zgq
 * @date: 2024年04月26日 11:28
 */
public class SingleCSVExporter extends BaseSingleExcelExporter implements SingleFileExporter {

    public SingleCSVExporter() {
        suffix = ExportFileSuffix.CSV.getSuffix();
        contentType = "text/csv";
    }

    /**
     * @return
     */
    @Override
    protected ExcelTypeEnum getExcelType() {
        return ExcelTypeEnum.CSV;
    }
}
