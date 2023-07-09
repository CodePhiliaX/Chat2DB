
package ai.chat2db.spi.ssh;

import ai.chat2db.server.tools.common.exception.ConnectionException;
import ai.chat2db.spi.model.SSHInfo;
import cn.hutool.core.net.NetUtil;
import cn.hutool.extra.ssh.JschUtil;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author jipengfei
 * @version : SSHSessionManager.java
 */
@Slf4j
public class SSHManager {

    public static Session getSSHSession(SSHInfo ssh) {
        Session session;
        try {
            byte[] passphrase = StringUtils.isNotBlank(ssh.getPassphrase()) ? StringUtils.getBytes(ssh.getPassphrase(),
                "UTF-8") : null;
            session = JschUtil.getSession(ssh.getHostName(), Integer.parseInt(ssh.getPort()), ssh.getUserName(),
                ssh.getKeyFile(), passphrase);

        } catch (Exception e) {
            throw new ConnectionException("connection.ssh.error", null, e);
        }
        if (StringUtils.isNotBlank(ssh.getRHost()) && StringUtils.isNotBlank(ssh.getRPort())) {
            try {
                int localPort = !StringUtils.isBlank(ssh.getLocalPort()) ? Integer.parseInt(ssh.getLocalPort())
                    : NetUtil.getUsableLocalPort();
                ssh.setLocalPort(String.valueOf(localPort));
                session.setPortForwardingL(localPort, ssh.getRHost(),
                    Integer.parseInt(ssh.getRPort()));
            } catch (Exception e) {
                if (session != null && session.isConnected()) {
                    session.disconnect();
                }
                throw new ConnectionException("connection.ssh.error", null, e);
            }
        }
        return session;
    }

    public static void close() {
        JschUtil.closeAll();
    }
}