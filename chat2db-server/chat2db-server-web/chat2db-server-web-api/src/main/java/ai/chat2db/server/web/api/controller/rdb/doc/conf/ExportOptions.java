package ai.chat2db.server.web.api.controller.rdb.doc.conf;

import lombok.Data;

/**
 * Build options
 *
 * @author lzy
 */
@Data
public class ExportOptions {
    /**
     * Whether to export multiple sheets
     */
    private Boolean isExportMoreSheet = Boolean.FALSE;
    /**
     * Whether to export the index
     */
    private Boolean isExportIndex = Boolean.FALSE;

    /**
     * Export file suffix
     **/
    private String fileSuffix;
}
