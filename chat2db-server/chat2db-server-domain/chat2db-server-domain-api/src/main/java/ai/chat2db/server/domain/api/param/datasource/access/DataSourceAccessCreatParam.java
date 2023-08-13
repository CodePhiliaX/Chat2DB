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
     * 数据源id
     */
    @NotNull
    private Long dataSourceId;

    /**
     * 授权类型
     *
     * @see AccessObjectTypeEnum
     */
    @NotNull
    private String accessObjectType;

    /**
     * 授权id,根据类型区分是用户还是团队
     */
    @NotNull
    private Long accessObjectId;
}
