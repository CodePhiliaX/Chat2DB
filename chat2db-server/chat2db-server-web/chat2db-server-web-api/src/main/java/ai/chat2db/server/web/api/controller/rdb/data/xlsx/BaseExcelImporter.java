package ai.chat2db.server.web.api.controller.rdb.data.xlsx;

import ai.chat2db.server.tools.common.model.data.option.BaseImportExcelDataOptions;
import ai.chat2db.server.tools.common.model.data.option.AbstractImportDataOptions;
import ai.chat2db.server.web.api.controller.rdb.data.BaseFileImporter;
import com.alibaba.excel.EasyExcel;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

/**
 * @author: zgq
 * @date: 2024年04月27日 11:16
 */
public class BaseExcelImporter extends BaseFileImporter {
    @Override
    protected void doImportData(Connection connection, String databaseName, String schemaName, String tableName,
                                List<String> tableColumns, List<String> fileColumns,
                                AbstractImportDataOptions importDataOption, MultipartFile file) throws IOException {
        NoModelDataListener noModelDataListener = new NoModelDataListener(schemaName, tableName, tableColumns,
                                                                          fileColumns, importDataOption, connection);
        Integer headerRowNum = ((BaseImportExcelDataOptions) importDataOption).getHeaderRowNum();
        EasyExcel.read(file.getInputStream(), noModelDataListener)
                .sheet()
                .headRowNumber(headerRowNum)
                .doRead();

    }
}
