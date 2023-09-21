package ai.chat2db.server.tools.common.config;

import ai.chat2db.server.tools.common.util.ConfigUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * 全局字典
 *
 * @author lzy
 */
public interface GlobalDict {
    /**
     * 模板文件
     **/
    List<String> TEMPLATE_FILE = Arrays.asList("template.html", "template_diy.docx", "sub_template_diy.docx");
    /**
     * 模板存放目录
     **/
    String templateDir = ConfigUtils.CONFIG_BASE_PATH + File.separator + "template" + File.separator;

}
