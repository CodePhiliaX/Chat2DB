package ai.chat2db.server.web.api.controller.rdb.data.xls;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.web.api.controller.rdb.data.DataFileExporter;
import ai.chat2db.server.web.api.controller.rdb.data.xlsx.BaseExcelExporter;
import com.alibaba.excel.support.ExcelTypeEnum;

/**
 * @author: zgq
 * @date: 2024年04月29日 21:48
 */
public class XLSExporter extends BaseExcelExporter implements DataFileExporter {

    public XLSExporter() {
        suffix = ExportFileSuffix.XLS.getSuffix();
        contentType = "application/vnd.ms-excel";
    }
    @Override
    protected ExcelTypeEnum getExcelType() {
        return ExcelTypeEnum.XLS;
    }
}
