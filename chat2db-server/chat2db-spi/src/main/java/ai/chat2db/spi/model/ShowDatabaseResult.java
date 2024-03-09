
package ai.chat2db.spi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author jipengfei
 * @version : ShowDatabaseResult.java
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ShowDatabaseResult implements Serializable {
    private static final long serialVersionUID = 1L;
    String database;
}