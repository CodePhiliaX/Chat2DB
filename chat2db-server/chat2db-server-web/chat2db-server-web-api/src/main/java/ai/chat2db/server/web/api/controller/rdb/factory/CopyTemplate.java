package ai.chat2db.server.web.api.controller.rdb.factory;

import ai.chat2db.server.tools.common.util.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class CopyTemplate {
    /**
     * 模板文件
     **/
    private static final List<String> TEMPLATE_FILE = Arrays.asList("template.html", "template_diy.docx", "sub_template_diy.docx");

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

    private static void saveFile(String dir, String path, boolean isOverride) {
        if (!isOverride) {
            File file = new File(dir + File.separator + path);
            if (file.exists()) {
                return;
            }
        }
        try (// 模板文件输入输出地址 读取resources下文件
             FileOutputStream outputStream = new FileOutputStream(dir + File.separator + path);
             //返回读取指定资源的输入流
             InputStream inputStream = CopyTemplate.class.getClassLoader().getResourceAsStream("template/" + path)) {
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
