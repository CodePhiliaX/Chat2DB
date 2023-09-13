package ai.chat2db.server.domain.api.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import ai.chat2db.server.domain.api.enums.AccessObjectTypeEnum;
import ai.chat2db.server.domain.api.param.datasource.access.DataSourceAccessSelector;
import ai.chat2db.server.tools.base.constant.EasyToolsConstant;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * DataSource Access
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceAccess implements Serializable {

    @Serial
    private static final long serialVersionUID = EasyToolsConstant.SERIAL_VERSION_UID;

    /**
     * 主键
     */
    @NotNull
    private Long id;

    /**
     * 创建时间
     */
    @NotNull
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    @NotNull
    private LocalDateTime gmtModified;

    /**
     * 创建人用户id
     */
    private Long createUserId;

    /**
     * 修改人用户id
     */
    private Long modifiedUserId;

    /**
     * 数据源id
     */
    @NotNull
    private Long dataSourceId;

    /**
     * 数据源
     */
    @NotNull
    private DataSource dataSource;

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
     * @see DataSourceAccessSelector#setAccessObject(Boolean)
     */
    @NotNull
    private DataSourceAccessObject accessObject;
}
