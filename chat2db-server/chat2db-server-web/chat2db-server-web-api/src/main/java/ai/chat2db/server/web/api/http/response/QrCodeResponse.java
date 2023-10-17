package ai.chat2db.server.web.api.http.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class QrCodeResponse {
    /**
     * When logging in for the first time, the token will be returned, and subsequent services need to poll the token to
     * check if the user is logged in
     */
    private String token;

    /**
     * Return the QR code used by the user to log in
     */
    private String wechatQrCodeUrl;

    /**
     * If the user logs in successfully, it will return apiKey. If the front-end detects the presence of apiKey, it
     * indicates successful login
     */
    private String apiKey;

    private String tip;
}
