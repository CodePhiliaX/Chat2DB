
package ai.chat2db.spi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jipengfei
 * @version : ShowDatabaseResult.java
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ShowDatabaseResult {
    String database;
}