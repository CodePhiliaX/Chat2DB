package ai.chat2db.spi.model;

import ai.chat2db.spi.enums.DataTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 单元格头
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Header {
    /**
     * 单元格类型
     *
     * @see DataTypeEnum
     */
    private String dataType;

    /**
     * 展示的名字
     */
    private String name;
}
