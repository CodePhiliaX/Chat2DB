
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
     * 是否使用ssh
     */
    private boolean use;

    /**
     * ssh主机名
     */
    private String hostName;

    /**
     * ssh端口
     */
    private String port;

    /**
     * ssh用户名
     */
    private String userName;

    /**
     * ssh本地端口
     */
    private String localPort;

    /**
     * ssh认证类型
     */
    private String authenticationType;

    /**
     * ssh密码
     */
    private String password;

    /**
     * ssh密钥文件
     */
    private String keyFile;

    /**
     * ssh密钥文件密码
     */
    private String passphrase;

    /**
     * ssh跳板机目标主机
     */
    private String rHost;

    /**
     * ssh跳板目标端口
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