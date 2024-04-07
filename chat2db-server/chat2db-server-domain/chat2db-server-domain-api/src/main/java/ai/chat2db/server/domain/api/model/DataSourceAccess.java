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
     * primary key
     */
    @NotNull
    private Long id;

    /**
     * creation time
     */
    @NotNull
    private LocalDateTime gmtCreate;

    /**
     * modified time
     */
    @NotNull
    private LocalDateTime gmtModified;

    /**
     * Creator user id
     */
    private Long createUserId;

    /**
     * Modifier user id
     */
    private Long modifiedUserId;

    /**
     * Data source id
     */
    @NotNull
    private Long dataSourceId;

    /**
     * data source
     */
    @NotNull
    private DataSource dataSource;

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

    /**
     * Authorization object
     * @see DataSourceAccessSelector#setAccessObject(Boolean)
     */
    @NotNull
    private DataSourceAccessObject accessObject;
}
