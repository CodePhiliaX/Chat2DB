package ai.chat2db.server.domain.api.model;

import java.io.Serial;
import java.io.Serializable;

import ai.chat2db.server.domain.api.enums.AccessObjectTypeEnum;
import ai.chat2db.server.tools.base.constant.EasyToolsConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * DataSource Access Object
 * It could be a user or a team
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceAccessObject implements Serializable {

    @Serial
    private static final long serialVersionUID = EasyToolsConstant.SERIAL_VERSION_UID;

    /**
     * Authorization ID, distinguish whether it is a user or a team according to the type
     */
    private Long id;

    /**
     * Authorization type
     *
     * @see AccessObjectTypeEnum
     */
    private String type;

    /**
     * The name of the code that belongs to the authorization type, such as user account, team code
     */
    private String code;

    /**
     * Code that belongs to the authorization type, such as user name, team name
     */
    private String name;
}
