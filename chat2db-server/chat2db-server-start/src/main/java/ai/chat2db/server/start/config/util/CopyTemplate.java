package ai.chat2db.server.start.config.util;

import ai.chat2db.server.tools.common.util.ConfigUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * CopyTemplate
 *
 * @author lzy
 **/
@Component
@Slf4j
public class CopyTemplate {
    /**
     * 模板文件
     **/
    public static final List<String> TEMPLATE_FILE = Arrays.asList("template.html", "template_diy.docx", "sub_template_diy.docx");

    static {
        //复制模板
        copyTemplateFile();
    }

    public static void copyTemplateFile() {
        String templateDir = ConfigUtils.CONFIG_BASE_PATH + File.separator + "template";
        File file = new File(templateDir);
        if (!file.exists()) {
            file.mkdir();
        }
        for (String template : TEMPLATE_FILE) {
            saveFile(templateDir, template, true);
        }
    }

    public static void saveFile(String dir, String path, boolean isOverride) {
        if (!isOverride) {
            File file = new File(dir + File.separator + path);
            if (file.exists()) {
                return;
            }
        }
        try (// 模板文件输入输出地址 读取resources下文件
             FileOutputStream outputStream = new FileOutputStream(dir + File.separator + path);
             //返回读取指定资源的输入流
             InputStream inputStream = ConfigUtils.class.getClassLoader().getResourceAsStream("template" + File.separator + path)) {
            byte[] buffer = new byte[4096];
            int n = 0;
            while (-1 != (n = inputStream.read(buffer))) {
                outputStream.write(buffer, 0, n);
            }
            outputStream.flush();
        } catch (Exception e) {
            log.error("saveFile error", e);
        }
    }
}
