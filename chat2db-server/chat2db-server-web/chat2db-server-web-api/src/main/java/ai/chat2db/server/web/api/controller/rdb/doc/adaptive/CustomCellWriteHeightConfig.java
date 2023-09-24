package ai.chat2db.server.web.api.controller.rdb.doc.adaptive;

import com.alibaba.excel.write.style.row.AbstractRowHeightStyleStrategy;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.util.Iterator;

/**
 * CustomCellWriteHeightConfig
 *
 * @author lzy
 **/
public class CustomCellWriteHeightConfig  extends AbstractRowHeightStyleStrategy {
    /**
     * 默认高度
     */
    private static final Integer DEFAULT_HEIGHT = 300;

    @Override
    protected void setHeadColumnHeight(Row row, int relativeRowIndex) {
    }

    @Override
    protected void setContentColumnHeight(Row row, int relativeRowIndex) {
        Iterator<Cell> cellIterator = row.cellIterator();
        if (!cellIterator.hasNext()) {
            return;
        }

        // 默认为 1行高度
        int maxHeight = 1;
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            if (cell.getCellType() == CellType.STRING) {
                if (cell.getStringCellValue().contains("\n")) {
                    int length = cell.getStringCellValue().split("\n").length;
                    maxHeight = Math.max(maxHeight, length);
                }
            }
        }

        row.setHeight((short) (maxHeight * DEFAULT_HEIGHT));
    }
}