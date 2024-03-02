package ai.chat2db.spi.model;

import java.math.BigDecimal;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * cell type
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Cell {

    /**
     * cell type
     *
     * @see CellTypeEnum
     */
    private String type;

    /**
     * string data
     */
    private String stringValue;

    /**
     * number
     */
    private BigDecimal bigDecimalValue;

    /**
     * date data
     */
    private Long dateValue;

    /**
     * binary stream
     */
    private byte[] byteValue;
}
