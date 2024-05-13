package ai.chat2db.server.web.start.controller.oauth.request;

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
    @NotNull(message = "Username can not be empty")
    private String userName;

    /**
     * password
     */
    @NotNull(message = "password can not be blank")
    private String password;
}
