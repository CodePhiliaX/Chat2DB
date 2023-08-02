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
public class MetaDataQueryParam {

    @NotNull
    private Long dataSourceId;


    /**
     * if true, refresh the cache
     */
    private boolean refresh;
}
