package ai.chat2db.server.web.api.controller.rdb.doc.export;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.domain.api.enums.ExportTypeEnum;
import ai.chat2db.server.domain.api.model.IndexInfo;
import ai.chat2db.server.domain.api.model.TableParameter;
import ai.chat2db.server.tools.common.util.I18nUtils;
import ai.chat2db.server.web.api.controller.rdb.doc.DatabaseExportService;
import ai.chat2db.server.web.api.controller.rdb.doc.conf.ExportOptions;
import ai.chat2db.server.web.api.controller.rdb.doc.constant.PatternConstant;
import ai.chat2db.server.web.api.util.StringUtils;
import lombok.SneakyThrows;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ExportMarkdownService
 *
 * @author lzy
 **/
public class ExportMarkdownService extends DatabaseExportService {

    public ExportMarkdownService() {
        exportTypeEnum = ExportTypeEnum.MARKDOWN;
        suffix = ExportFileSuffix.MARKDOWN.getSuffix();
        contentType = "text/plain";
    }

    @SneakyThrows
    @Override
    public void export(OutputStream outputStream, ExportOptions exportOptions) {
        Map<String, List<Map.Entry<String, List<TableParameter>>>> allMap = listMap.entrySet()
                .stream().collect(Collectors.groupingBy(v -> v.getKey().split("---")[0]));
        try (BufferedWriter fileWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
            for (Map.Entry<String, List<Map.Entry<String, List<TableParameter>>>> myMap : allMap.entrySet()) {
                //数据库名
                String database = myMap.getKey();
                String title = String.format(PatternConstant.TITLE, I18nUtils.getMessage("main.databaseText") + database);
                fileWriter.write(title);
                writeLineSeparator(fileWriter, 2);
                for (Map.Entry<String, List<TableParameter>> parameterMap : myMap.getValue()) {
                    //表名
                    String tableName = parameterMap.getKey().split("---")[1];
                    fileWriter.write(String.format(PatternConstant.CATALOG, tableName));
                    writeLineSeparator(fileWriter, 1);
                    //索引Table
                    if (!indexMap.isEmpty()) {
                        fileWriter.write(PatternConstant.ALL_INDEX_TABLE_HEADER);
                        writeLineSeparator(fileWriter, 1);
                        fileWriter.write(PatternConstant.INDEX_TABLE_SEPARATOR);
                        writeLineSeparator(fileWriter, 1);
                        String name = parameterMap.getKey().split("\\[")[0];
                        List<IndexInfo> indexInfoVOList = indexMap.get(name);
                        for (int j = 0; j < indexInfoVOList.size(); j++) {
                            fileWriter.write(String.format(PatternConstant.INDEX_TABLE_BODY, getIndexValues(indexInfoVOList.get(j))));
                            writeLineSeparator(fileWriter, 1);
                        }
                        writeLineSeparator(fileWriter, 1);
                    }
                    writeLineSeparator(fileWriter, 2);
                    fileWriter.write(PatternConstant.ALL_TABLE_HEADER);
                    writeLineSeparator(fileWriter, 1);
                    fileWriter.write(PatternConstant.TABLE_SEPARATOR);
                    writeLineSeparator(fileWriter, 1);
                    //字段Table
                    List<TableParameter> exportList = parameterMap.getValue();
                    for (TableParameter tableParameter : exportList) {
                        fileWriter.write(String.format(PatternConstant.TABLE_BODY, getColumnValues(tableParameter)));
                        writeLineSeparator(fileWriter, 1);
                    }
                    writeLineSeparator(fileWriter, 2);
                }
            }
        }
    }

    private void writeLineSeparator(BufferedWriter fileWriter, int number) throws IOException {
        for (int i = 0; i < number; i++) {
            fileWriter.write(System.lineSeparator());
        }
    }

    @Override
    public String dealWith(String source) {
        return StringUtils.isNullForHtml(source);
    }
}
