
package ai.chat2db.spi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jipengfei
 * @version : Trigger.java
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Trigger {

    private String databaseName;

    private String schemaName;

    private String triggerName;

    private String eventManipulation;

    private String triggerBody;

}