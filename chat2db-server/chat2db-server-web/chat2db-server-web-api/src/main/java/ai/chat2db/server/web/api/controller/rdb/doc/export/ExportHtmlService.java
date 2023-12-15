package ai.chat2db.server.web.api.controller.rdb.doc.export;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.domain.api.enums.ExportTypeEnum;
import ai.chat2db.server.domain.api.model.IndexInfo;
import ai.chat2db.server.domain.api.model.TableParameter;
import ai.chat2db.server.tools.common.config.GlobalDict;
import ai.chat2db.server.tools.common.util.I18nUtils;
import ai.chat2db.server.web.api.controller.rdb.doc.DatabaseExportService;
import ai.chat2db.server.web.api.controller.rdb.doc.conf.ExportOptions;
import ai.chat2db.server.web.api.controller.rdb.doc.constant.PatternConstant;
import ai.chat2db.server.web.api.util.StringUtils;
import lombok.SneakyThrows;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ExportHtmlService
 *
 * @author lzy
 **/
public class ExportHtmlService extends DatabaseExportService {

    public ExportHtmlService() {
        exportTypeEnum = ExportTypeEnum.HTML;
        suffix = ExportFileSuffix.HTML.getSuffix();
        contentType = "text/html";
    }

    @SneakyThrows
    @Override
    public void export(OutputStream outputStream, ExportOptions exportOptions) {
        Map<String, List<Map.Entry<String, List<TableParameter>>>> allMap = listMap.entrySet()
                .stream().collect(Collectors.groupingBy(v -> v.getKey().split("---")[0]));
        StringBuilder htmlText = new StringBuilder();
        StringBuilder catalogue = new StringBuilder();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
            for (Map.Entry<String, List<Map.Entry<String, List<TableParameter>>>> myMap : allMap.entrySet()) {
                //数据库名
                String database = myMap.getKey();
                String title = MessageFormat.format(PatternConstant.HTML_TITLE, I18nUtils.getMessage("main.databaseText") + database);
                //数据库名-目录
                catalogue.append("<li>").append(MessageFormat.format(PatternConstant.HTML_INDEX_ITEM, I18nUtils.getMessage("main.databaseText")
                        + database, I18nUtils.getMessage("main.databaseText") + database)).append("<ol>");
                htmlText.append(title).append("\n");
                for (Map.Entry<String, List<TableParameter>> parameterMap : myMap.getValue()) {
                    //表名
                    String tableName = parameterMap.getKey().split("---")[1];
                    //表名-目录
                    catalogue.append("<li>").append(MessageFormat.format(PatternConstant.HTML_INDEX_ITEM, database + tableName, tableName));
                    htmlText.append(MessageFormat.format(PatternConstant.HTML_CATALOG, database + tableName, tableName)).append("\n<p></p>");
                    //索引Table
                    if (!indexMap.isEmpty()) {
                        htmlText.append("<table>\n");
                        htmlText.append(PatternConstant.HTML_INDEX_TABLE_HEADER);
                        String name = parameterMap.getKey().split("\\[")[0];
                        List<IndexInfo> indexInfoVOList = indexMap.get(name);
                        for (IndexInfo indexInfo : indexInfoVOList) {
                            htmlText.append(String.format(PatternConstant.HTML_INDEX_TABLE_BODY, getIndexValues(indexInfo)));
                        }
                        htmlText.append("</table>\n");
                        htmlText.append("\n<p></p>");
                    } else {
                        htmlText.append(String.format(PatternConstant.HTML_INDEX_TABLE_BODY, getIndexValues(new IndexInfo())));
                    }
                    //字段Table
                    htmlText.append("<table>\n");
                    htmlText.append(PatternConstant.HTML_TABLE_HEADER);
                    List<TableParameter> exportList = parameterMap.getValue();
                    for (TableParameter tableParameter : exportList) {
                        htmlText.append(String.format(PatternConstant.HTML_TABLE_BODY, getColumnValues(tableParameter)));
                    }
                    htmlText.append("</table>\n");
                }
                htmlText.append("<p></p>");
                catalogue.append("</ol>");
            }
            catalogue.append("</li>");

            try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/" + GlobalDict.TEMPLATE_FILE.get(0));
                 ByteArrayOutputStream result = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }
                String str = result.toString(String.valueOf(StandardCharsets.UTF_8));

                str = str.replace("${data}", htmlText).replace("${catalogue}", catalogue);
                writer.write(str);
            }
        }
    }

    @Override
    public String dealWith(String source) {
        return StringUtils.isNullForHtml(source);
    }
}
