package ai.chat2db.server.web.api.controller.rdb.doc.merge;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.merge.AbstractMergeStrategy;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * MyMergeExcel
 *
 * @author lzy
 **/
public class MyMergeExcel extends AbstractMergeStrategy {

    public static final String NAME = "isTableNameBlank";

    @Override
    protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {
        if (NAME.equals(cell.getStringCellValue())) {
            CellRangeAddress region = new CellRangeAddress(cell.getRowIndex(), cell.getRowIndex(), 0, 7);
            sheet.addMergedRegion(region);
            Row row = sheet.getRow(cell.getRowIndex());
            cell = row.getCell(0);
            Workbook workbook = sheet.getWorkbook();
            // generate a style
            CellStyle style = workbook.createCellStyle();
            // Set these styles
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            //Set up filling scheme
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            //Set custom fill color
            style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            // generate a font
            Font font = workbook.createFont();
            font.setBold(true);
            font.setFontHeightInPoints((short) 14);
            // Apply font to current style
            style.setFont(font);
            cell.setCellStyle(style);
        }
    }
}