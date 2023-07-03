
package ai.chat2db.spi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jipengfei
 * @version : TableSchema.java
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Schema {

    /**
     * databaseName
     */
    private String databaseName;
    /**
     * 数据名字
     */
    private String name;
}