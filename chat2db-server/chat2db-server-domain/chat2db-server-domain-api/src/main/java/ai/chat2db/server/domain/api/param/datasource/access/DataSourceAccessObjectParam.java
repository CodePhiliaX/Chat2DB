
package ai.chat2db.server.domain.api.param.datasource.access;

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
public class DataSourceAccessObjectParam implements Serializable {

    @Serial
    private static final long serialVersionUID = EasyToolsConstant.SERIAL_VERSION_UID;

    /**
     * 授权id,根据类型区分是用户还是团队
     */
    private Long id;

    /**
     * 授权类型
     *
     * @see AccessObjectTypeEnum
     */
    private String type;

}
