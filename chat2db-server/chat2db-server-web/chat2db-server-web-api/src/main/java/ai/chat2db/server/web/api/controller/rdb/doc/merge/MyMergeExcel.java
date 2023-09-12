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
            // 生成一个样式
            CellStyle style = workbook.createCellStyle();
            // 设置这些样式
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            //设置填充方案
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            //设置自定义填充颜色
            style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            // 生成一个字体
            Font font = workbook.createFont();
            font.setBold(true);
            font.setFontHeightInPoints((short) 14);
            // 把字体应用到当前的样式
            style.setFont(font);
            cell.setCellStyle(style);
        }
    }
}