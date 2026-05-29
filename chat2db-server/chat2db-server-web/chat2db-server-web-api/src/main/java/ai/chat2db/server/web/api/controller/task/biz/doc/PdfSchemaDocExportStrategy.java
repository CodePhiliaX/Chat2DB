package ai.chat2db.server.web.api.controller.task.biz.doc;

import ai.chat2db.server.domain.api.enums.ExportTypeEnum;
import ai.chat2db.server.domain.api.model.IndexInfo;
import ai.chat2db.server.domain.api.model.TableParameter;
import ai.chat2db.server.tools.common.util.I18nUtils;
import ai.chat2db.server.web.api.controller.rdb.doc.constant.CommonConstant;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PdfSchemaDocExportStrategy extends AbstractSchemaDocExportStrategy {

    @Override
    public boolean supports(String exportType) {
        return ExportTypeEnum.PDF.getCode().equals(exportType);
    }

    @Override
    protected void doExport(OutputStream outputStream, SchemaDocExportContext context) throws Exception {
        boolean isExportIndex = context.getExportOptions().getIsExportIndex();
        Map<String, List<Map.Entry<String, List<TableParameter>>>> allMap = context.getTableParameterMap().entrySet()
                .stream().collect(Collectors.groupingBy(v -> v.getKey().split("---")[0]));

        Document document = new Document();
        PdfWriter pdfWriter = PdfWriter.getInstance(document, outputStream);
        pdfWriter.setStrictImageSequence(true);

        BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font font = new Font(baseFont, 10, Font.NORMAL);
        Font headFont = new Font(baseFont, 12, Font.NORMAL);
        Font titleFont = new Font(baseFont, 14, Font.BOLD);

        document.open();

        for (Map.Entry<String, List<Map.Entry<String, List<TableParameter>>>> myMap : allMap.entrySet()) {
            String database = myMap.getKey();
            String title = I18nUtils.getMessage("main.databaseText") + database;
            Paragraph p = new Paragraph(title, titleFont);
            document.add(p);

            for (Map.Entry<String, List<TableParameter>> parameterMap : myMap.getValue()) {
                String tableName = parameterMap.getKey().split("---")[1];
                Paragraph tableParagraph = new Paragraph(tableName, font);
                document.add(tableParagraph);

                if (isExportIndex && !context.getIndexMap().isEmpty()) {
                    PdfPTable table = new PdfPTable(CommonConstant.INDEX_HEAD_NAMES.length);
                    process(table, CommonConstant.INDEX_HEAD_NAMES, headFont);
                    String name = parameterMap.getKey().split("\\[")[0];
                    List<IndexInfo> indexInfoVOList = context.getIndexMap().get(name);
                    if (indexInfoVOList != null) {
                        for (IndexInfo indexInfo : indexInfoVOList) {
                            processWithObjects(table, getIndexValues(indexInfo), font);
                        }
                    }
                    table.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
                    table.setSpacingBefore(10f);
                    table.setSpacingAfter(20f);
                    document.add(table);
                }
                document.add(new Paragraph());

                List<TableParameter> exportList = parameterMap.getValue();
                PdfPTable table = new PdfPTable(CommonConstant.COLUMN_HEAD_NAMES.length);
                process(table, CommonConstant.COLUMN_HEAD_NAMES, headFont);
                for (TableParameter tableParameter : exportList) {
                    processWithObjects(table, getColumnValues(tableParameter), font);
                }
                table.setSpacingBefore(10f);
                table.setSpacingAfter(20f);
                table.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
                document.add(table);
            }
        }
        document.close();
    }

    public static <T> void process(PdfPTable table, T[] line, Font font) {
        for (T s : line) {
            if (Objects.isNull(s)) {
                continue;
            }
            PdfPCell cell = new PdfPCell(new Paragraph(s.toString(), font));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setPaddingTop(5);
            cell.setPaddingBottom(5);
            table.addCell(cell);
        }
    }

    private static void processWithObjects(PdfPTable table, Object[] line, Font font) {
        for (Object obj : line) {
            String value = Objects.isNull(obj) ? "" : obj.toString();
            PdfPCell cell = new PdfPCell(new Paragraph(value, font));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setPaddingTop(5);
            cell.setPaddingBottom(5);
            table.addCell(cell);
        }
    }
}
