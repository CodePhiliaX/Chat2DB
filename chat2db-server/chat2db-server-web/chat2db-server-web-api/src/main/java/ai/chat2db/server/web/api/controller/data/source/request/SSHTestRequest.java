
package ai.chat2db.server.web.api.controller.data.source.request;

import lombok.Data;

/**
 * @author jipengfei
 * @version : SSHTestRequest.java
 */
@Data
public class SSHTestRequest {
    private boolean use;

    private String hostName;

    private String port;

    private String userName;

    private String localPort;

    private String authenticationType;

    private String password;

    private String keyFile;

    private String passphrase;
}