
package ai.chat2db.server.domain.api.param;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jipengfei
 * @version : SchemaQueryParam.java
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SchemaQueryParam {

    @NotNull
    private Long dataSourceId;

    private String dataBaseName;
}