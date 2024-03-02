
package ai.chat2db.server.web.api.controller.user.request;

import java.io.Serial;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jipengfei
 * @version : UserCreateRequest.java
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 353710386092262213L;
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