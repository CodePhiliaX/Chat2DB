
package ai.chat2db.spi.model;

import java.util.Objects;

import lombok.Data;

/**
 * @author jipengfei
 * @version : SSHInfo.java
 */
@Data
public class SSHInfo {

    /**
     * Whether to use ssh
     */
    private boolean use;

    /**
     * ssh hostname
     */
    private String hostName;

    /**
     * ssh port
     */
    private String port;

    /**
     * ssh username
     */
    private String userName;

    /**
     * ssh local port
     */
    private String localPort;

    /**
     * ssh Certification type
     */
    private String authenticationType;

    /**
     * ssh password
     */
    private String password;

    /**
     * ssh key file
     */
    private String keyFile;

    /**
     * ssh key file password
     */
    private String passphrase;

    /**
     * ssh springboard target host
     */
    private String rHost;

    /**
     * ssh springboard target port
     */
    private String rPort;

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        SSHInfo sshInfo = (SSHInfo)o;
        return use == sshInfo.use && Objects.equals(hostName, sshInfo.hostName) && Objects.equals(port,
            sshInfo.port) && Objects.equals(userName, sshInfo.userName) && Objects.equals(localPort,
            sshInfo.localPort) && Objects.equals(authenticationType, sshInfo.authenticationType)
            && Objects.equals(password, sshInfo.password) && Objects.equals(keyFile, sshInfo.keyFile)
            && Objects.equals(passphrase, sshInfo.passphrase) && Objects.equals(rHost, sshInfo.rHost)
            && Objects.equals(rPort, sshInfo.rPort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(use, hostName, port, userName, localPort, authenticationType, password, keyFile, passphrase,
            rHost, rPort);
    }
}