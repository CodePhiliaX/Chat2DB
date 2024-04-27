package ai.chat2db.server.web.api.controller.rdb.data.xlsx;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.web.api.controller.rdb.data.BaseMultiExcelExporter;
import com.alibaba.excel.support.ExcelTypeEnum;

/**
 * @author: zgq
 * @date: 2024年04月26日 14:06
 */
public class MultiXLSXExporter extends BaseMultiExcelExporter {


    public MultiXLSXExporter() {
        suffix = ExportFileSuffix.EXCEL.getSuffix();
    }

    @Override
    protected ExcelTypeEnum getExcelType() {
        return ExcelTypeEnum.XLSX;
    }
}
