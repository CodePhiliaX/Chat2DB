package ai.chat2db.spi.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: zgq
 * @date: 2024年05月16日 23:20
 */
@Data
@Accessors(chain = true)
public class TableConstraintColumn {
    private String columnName;
    private String AscOrDesc;
}
