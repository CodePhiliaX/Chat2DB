
package ai.chat2db.server.domain.api.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jipengfei
 * @version : SchemaOperationParam.java
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SchemaOperationParam {
    String databaseName;
    String schemaName;
    String newSchemaName;
}