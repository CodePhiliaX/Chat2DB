
package ai.chat2db.server.web.api.controller.user.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jipengfei
 * @version : UserVO.java
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserVO implements Serializable {
    private static final long serialVersionUID = 502943167829222727L;

    /**
     * primary key
     */
    private Long id;

    /**
     * userName
     */
    private String userName;

    /**
     * password
     */
    private String password;

    /**
     * nickName
     */
    private String nickName;


    /**
     * email
     */
    private String email;
}