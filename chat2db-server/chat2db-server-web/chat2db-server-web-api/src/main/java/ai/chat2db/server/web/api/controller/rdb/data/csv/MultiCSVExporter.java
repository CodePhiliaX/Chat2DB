package ai.chat2db.server.web.api.controller.rdb.data.csv;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.web.api.controller.rdb.data.BaseMultiExcelExporter;
import com.alibaba.excel.support.ExcelTypeEnum;

/**
 * @author: zgq
 * @date: 2024年04月26日 11:29
 */
public class MultiCSVExporter extends BaseMultiExcelExporter {

    public MultiCSVExporter() {
        suffix = ExportFileSuffix.CSV.getSuffix();
    }


    @Override
    protected ExcelTypeEnum getExcelType() {
        return ExcelTypeEnum.CSV;

    }
}
