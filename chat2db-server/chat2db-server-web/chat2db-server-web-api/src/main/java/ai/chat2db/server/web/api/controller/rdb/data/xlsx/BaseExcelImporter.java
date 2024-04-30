package ai.chat2db.server.web.api.controller.rdb.data.xlsx;

import ai.chat2db.server.tools.common.model.rdb.data.option.AbstractImportDataOptions;
import ai.chat2db.server.tools.common.model.rdb.data.option.BaseImportExcelDataOptions;
import ai.chat2db.server.web.api.controller.rdb.data.AbstractDataFileImporter;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

/**
 * @author: zgq
 * @date: 2024年04月27日 11:16
 */
@Slf4j
public abstract class BaseExcelImporter extends AbstractDataFileImporter {
    @Override
    protected void doImportData(Connection connection, String databaseName, String schemaName, String tableName,
                                List<String> tableColumns, List<String> fileColumns,
                                AbstractImportDataOptions importDataOption, MultipartFile file) throws IOException {
        ExcelTypeEnum excelType = getExcelType();
        log.info("import {} data file", excelType.name());
        NoModelDataListener noModelDataListener = new NoModelDataListener(databaseName,schemaName, tableName, tableColumns,
                                                                          fileColumns, importDataOption, connection);
        Integer headerRowNum = ((BaseImportExcelDataOptions) importDataOption).getHeaderRowNum();
        EasyExcel.read(file.getInputStream(), noModelDataListener)
                .excelType(excelType)
                .sheet()
                .headRowNumber(headerRowNum)
                .doRead();

    }

    protected abstract ExcelTypeEnum getExcelType();
}
