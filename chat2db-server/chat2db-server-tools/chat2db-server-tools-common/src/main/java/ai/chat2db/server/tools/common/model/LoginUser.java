package ai.chat2db.server.tools.common.model;

import java.io.Serial;
import java.io.Serializable;

import ai.chat2db.server.tools.base.constant.EasyToolsConstant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Login user information
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser implements Serializable {
    @Serial
    private static final long serialVersionUID = EasyToolsConstant.SERIAL_VERSION_UID;

    /**
     * user id
     */
    private Long id;

    /**
     * nick name
     */
    private String nickName;

    /**
     * Is it an administrator
     */
    private Boolean admin;

    /**
     * role coding
     *
     * @see RoleCodeEnum
     */
    private String roleCode;


    private String token;
}
