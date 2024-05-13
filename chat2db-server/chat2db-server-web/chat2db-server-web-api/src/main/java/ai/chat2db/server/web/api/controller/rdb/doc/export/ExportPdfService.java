package ai.chat2db.server.web.api.controller.rdb.doc.export;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.domain.api.enums.ExportTypeEnum;
import ai.chat2db.server.domain.api.model.IndexInfo;
import ai.chat2db.server.domain.api.model.TableParameter;
import ai.chat2db.server.tools.common.util.I18nUtils;
import ai.chat2db.server.web.api.controller.rdb.doc.DatabaseExportService;
import ai.chat2db.server.web.api.controller.rdb.doc.conf.ExportOptions;
import ai.chat2db.server.web.api.controller.rdb.doc.constant.CommonConstant;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.SneakyThrows;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ExportPdfService
 *
 * @author lzy
 **/
public class ExportPdfService extends DatabaseExportService {

    public ExportPdfService() {
        exportTypeEnum = ExportTypeEnum.PDF;
        suffix = ExportFileSuffix.PDF.getSuffix();
        contentType = "application/pdf";
    }

    @SneakyThrows
    @Override
    public void export(OutputStream outputStream, ExportOptions exportOptions) {
        boolean isExportIndex = exportOptions.getIsExportIndex();
        Map<String, List<Map.Entry<String, List<TableParameter>>>> allMap = listMap.entrySet()
                .stream().collect(Collectors.groupingBy(v -> v.getKey().split("---")[0]));
        Document document = new Document();
        PdfWriter pdfWriter = PdfWriter.getInstance(document, outputStream);
        pdfWriter.setStrictImageSequence(true);
        // Font settings
        BaseFont baseFont =BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        // Create font object
        Font font = new Font(baseFont, 10, Font.NORMAL);
        Font headFont = new Font(baseFont, 12, Font.NORMAL);
        Font titleFont = new Font(baseFont, 14, Font.BOLD);
        document.open();
        //Iterate over data
        for (Map.Entry<String, List<Map.Entry<String, List<TableParameter>>>> myMap : allMap.entrySet()) {
            //Database name
            String database = myMap.getKey();
            String title = I18nUtils.getMessage("main.databaseText") + database;
            Paragraph p = new Paragraph(title, titleFont);
            document.add(p);
            for (Map.Entry<String, List<TableParameter>> parameterMap : myMap.getValue()) {
                //Table Name
                String tableName = parameterMap.getKey().split("---")[1];
                Paragraph tableParagraph = new Paragraph(tableName, font);
                document.add(tableParagraph);
                //IndexTable
                if (isExportIndex && !indexMap.isEmpty()) {
                    PdfPTable table = new PdfPTable(CommonConstant.INDEX_HEAD_NAMES.length);
                    process(table, CommonConstant.INDEX_HEAD_NAMES, font);
                    String name = parameterMap.getKey().split("\\[")[0];
                    List<IndexInfo> indexInfoVOList = indexMap.get(name);
                    for (IndexInfo indexInfo : indexInfoVOList) {
                        process(table, getIndexValues(indexInfo), font);
                    }
                    table.setPaddingTop(5);
                    document.add(table);
                }
                document.add(new Paragraph());
                //FieldTable
                List<TableParameter> exportList = parameterMap.getValue();
                PdfPTable table = new PdfPTable(CommonConstant.COLUMN_HEAD_NAMES.length);
                //title content
                process(table, CommonConstant.COLUMN_HEAD_NAMES, headFont);
                for (TableParameter tableParameter : exportList) {
                    process(table, getColumnValues(tableParameter), font);
                }
                // Set the blank space above the table, that is, the effect of moving downwards
                table.setSpacingBefore(10f);
                table.setSpacingAfter(20f);
                //Align left
                table.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
                document.add(table);
                //Pagination
                //document.newPage();
            }
        }
        document.close();
    }

    //Set table content
    public static <T> void process(PdfPTable table, T[] line, Font font) {
        for (T s : line) {
            if (Objects.isNull(s)) {
                return;
            }
            PdfPCell cell = new PdfPCell(new Paragraph(s.toString(), font));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setPaddingTop(5);
            cell.setPaddingBottom(5);
            table.addCell(cell);
        }
    }
}
