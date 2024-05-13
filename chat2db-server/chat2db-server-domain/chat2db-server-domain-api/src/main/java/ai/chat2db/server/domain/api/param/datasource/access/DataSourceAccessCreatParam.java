package ai.chat2db.server.domain.api.param.datasource.access;

import ai.chat2db.server.domain.api.enums.AccessObjectTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Data Source Access
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceAccessCreatParam  {
    /**
     * Data source id
     */
    @NotNull
    private Long dataSourceId;

    /**
     * Authorization type
     *
     * @see AccessObjectTypeEnum
     */
    @NotNull
    private String accessObjectType;

    /**
     * Authorization ID, distinguish whether it is a user or a team according to the type
     */
    @NotNull
    private Long accessObjectId;
}
