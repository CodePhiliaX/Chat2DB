package ai.chat2db.server.web.api.util;

/**
 * FileUtil
 *
 * @author lzy
 **/
public class FileUtils {

    public enum ConfigFile {
        // navicat连接信息文件
        NCX,
        // dbeaver连接信息文件
        DBP
    }

    public static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        } else {
            return "";
        }
    }

}
