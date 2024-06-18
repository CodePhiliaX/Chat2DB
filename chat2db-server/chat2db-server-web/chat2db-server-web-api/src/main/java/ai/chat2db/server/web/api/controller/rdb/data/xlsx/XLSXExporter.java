package ai.chat2db.server.web.api.controller.rdb.data.xlsx;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.web.api.controller.rdb.data.DataFileExporter;
import com.alibaba.excel.support.ExcelTypeEnum;

/**
 * @author: zgq
 * @date: 2024年04月26日 14:05
 */
public class XLSXExporter extends BaseExcelExporter implements DataFileExporter {

    public XLSXExporter() {
        suffix = ExportFileSuffix.XLSX.getSuffix();
        contentType = "application/vnd.ms-excel";
    }

    @Override
    protected ExcelTypeEnum getExcelType() {
        return ExcelTypeEnum.XLSX;
    }
}
