package ai.chat2db.server.domain.api.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * table structure selector
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TableSelector {
    /**
     * column list
     */
    private Boolean columnList;

    /**
     * index list
     */
    private Boolean indexList;

}