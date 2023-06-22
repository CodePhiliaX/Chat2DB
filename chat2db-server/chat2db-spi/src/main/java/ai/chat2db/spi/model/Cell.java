package ai.chat2db.spi.model;

import java.math.BigDecimal;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 单元格类型
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Cell {

    /**
     * 单元格类型
     *
     * @see CellTypeEnum
     */
    private String type;

    /**
     * 字符串数据
     */
    private String stringValue;

    /**
     * 数字
     */
    private BigDecimal bigDecimalValue;

    /**
     * 日期数据
     */
    private Long dateValue;

    /**
     * 二进制流
     */
    private byte[] byteValue;
}
