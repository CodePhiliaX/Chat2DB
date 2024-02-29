package ai.chat2db.server.domain.api.param;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TypeQueryParam {

    /**
     * Corresponding source id stored in the database
     */
    @NotNull
    private Long dataSourceId;

}
