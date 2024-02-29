package ai.chat2db.server.start.controller.oauth.request;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Log in
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    /**
     * userName
     */
    @NotNull(message = "用户名不能为空")
    private String userName;

    /**
     * password
     */
    @NotNull(message = "密码不能为空")
    private String password;
}
