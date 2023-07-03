
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
     * 主键
     */
    private Long id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickName;


    /**
     * 邮箱
     */
    private String email;
}