package ai.chat2db.spi.model;

import ai.chat2db.spi.enums.DataTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * cell header
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Header{
    /**
     * cell type
     *
     * @see DataTypeEnum
     */
    private String dataType;

    /**
     * display name
     */
    private String name;


    private Boolean primaryKey;


    private String comment;

    private String defaultValue;

    private Integer autoIncrement;

    private Integer nullable;

    private Integer columnSize;

    private Integer decimalDigits;

}
