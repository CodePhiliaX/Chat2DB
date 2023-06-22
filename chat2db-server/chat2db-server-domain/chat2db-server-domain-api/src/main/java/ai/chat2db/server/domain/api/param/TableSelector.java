package ai.chat2db.server.domain.api.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 表结构选择器
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TableSelector {
    /**
     * 列列表
     */
    private Boolean columnList;

    /**
     * 索引列表
     */
    private Boolean indexList;

}