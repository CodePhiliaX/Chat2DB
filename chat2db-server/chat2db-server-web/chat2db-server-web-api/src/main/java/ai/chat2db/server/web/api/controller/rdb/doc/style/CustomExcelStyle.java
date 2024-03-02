package ai.chat2db.server.web.api.controller.rdb.doc.style;

import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * CustomExcelStyle
 *
 * @author lzy
 **/
public class CustomExcelStyle {
    public static WriteCellStyle getContentWriteCellStyle() {
        //Content style strategy
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        //Center vertically, center horizontally
        contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        contentWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
        //Set automatic line wrapping
        contentWriteCellStyle.setWrapped(true);
        // Font strategy
        WriteFont contentWriteFont = new WriteFont();
        // font size
        contentWriteFont.setFontHeightInPoints((short) 11);
        contentWriteFont.setFontName("宋体");
        contentWriteFont.setColor(IndexedColors.BLACK.getIndex());
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        return contentWriteCellStyle;
    }

    public static WriteCellStyle getHeadStyle() {
        //Header policy uses default settings for font size
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();

        headWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        headWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        headWriteCellStyle.setBorderTop(BorderStyle.THIN);
        headWriteCellStyle.setBorderRight(BorderStyle.THIN);
        headWriteCellStyle.setBorderBottom(BorderStyle.THIN);

        headWriteCellStyle.setWrapped(false);

        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 14);
        headWriteFont.setBold(true);
        headWriteFont.setFontName("宋体");
        headWriteCellStyle.setWriteFont(headWriteFont);
        headWriteCellStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        return headWriteCellStyle;
    }
}
