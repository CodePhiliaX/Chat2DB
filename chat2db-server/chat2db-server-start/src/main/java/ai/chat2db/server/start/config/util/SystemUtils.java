package ai.chat2db.server.start.config.util;


import ai.chat2db.spi.ssh.SSHManager;
import lombok.extern.slf4j.Slf4j;

/**
 * 系统工具包
 *
 * @author Jiaju Zhuang
 */
@Slf4j
public class SystemUtils {

    /**
     * 停止当前应用
     */
    public static void stop() {
        new Thread(() -> {
            log.info("1秒以后退出应用");
            // 1秒以后自动退出应用
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // 直接系统退出
            log.info("开始退出系统应用");
            SSHManager.close();
            try {
                System.exit(0);
            } catch (Exception ignore) {
            }
        }).start();
    }
}
