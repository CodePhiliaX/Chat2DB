package ai.chat2db.server.domain.api.enums;

import lombok.Getter;

/**
 * ExportFileType
 *
 * @author lzy
 **/
@Getter
public enum ExportFileSuffix {

    //word
    WORD(".docx"),
    //excel
    EXCEL(".xlsx"),
    //markdown
    MARKDOWN(".md"),
    //html
    HTML(".html"),
    //pdf
    PDF(".pdf");

    private String suffix;

    ExportFileSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
