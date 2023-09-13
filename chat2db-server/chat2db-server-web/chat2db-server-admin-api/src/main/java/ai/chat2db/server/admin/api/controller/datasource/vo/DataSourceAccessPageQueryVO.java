
package ai.chat2db.server.admin.api.controller.datasource.vo;

import ai.chat2db.server.domain.api.enums.AccessObjectTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Pagination query
 *
 * @author Jiaju Zhuang
 */
@Data
public class DataSourceAccessPageQueryVO {

    /**
     * 主键
     */
    @NotNull
    private Long id;
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

    /**
     * 授权对象
     */
    @NotNull
    private DataSourceAccessObjectVO accessObject;
}
