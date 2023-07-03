
package ai.chat2db.spi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jipengfei
 * @version : CreateTableSql.java
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTableSql {

    public String tableName;

    public String sql;
}