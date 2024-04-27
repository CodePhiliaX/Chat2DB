package ai.chat2db.server.tools.common.model.data.option.json;

import ai.chat2db.server.tools.common.model.data.option.AbstractExportDataOptions;
import cn.hutool.core.date.DatePattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: zgq
 * @date: 2024年04月26日 0:20
 */
@Data
@EqualsAndHashCode(callSuper =true)
public class ExportData2JsonOptions extends AbstractExportDataOptions {

    private String dataTimeFormat;

    private Boolean isTimestamps;

    public ExportData2JsonOptions() {
        dataTimeFormat = DatePattern.NORM_DATETIME_PATTERN;
        isTimestamps = false;
    }
}
