package ai.chat2db.server.web.api.controller.rdb.doc.conf;

import lombok.Data;

/**
 * 生成选项
 *
 * @author lzy
 */
@Data
public class ExportOptions {
    /**
     * 是否导出多sheet
     */
    private Boolean isExportMoreSheet = Boolean.FALSE;
    /**
     * 是否导出索引
     */
    private Boolean isExportIndex = Boolean.FALSE;

    /**
     * 导出文件后缀
     **/
    private String fileSuffix;
}
