package ai.chat2db.server.web.api.controller.rdb.data.strategy;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.domain.api.param.datasource.DatabaseExportDataParam;
import ai.chat2db.server.web.api.controller.rdb.data.ExportDBDataStrategy;
import jakarta.servlet.http.HttpServletResponse;

public class ExportDBData2CsvStrategy extends ExportDBDataStrategy {
    
    public ExportDBData2CsvStrategy() {
        suffix = ExportFileSuffix.CSV.getSuffix();
        contentType = "";
    }

    @Override
    public void doExport(DatabaseExportDataParam param, HttpServletResponse response) {

    }
}
