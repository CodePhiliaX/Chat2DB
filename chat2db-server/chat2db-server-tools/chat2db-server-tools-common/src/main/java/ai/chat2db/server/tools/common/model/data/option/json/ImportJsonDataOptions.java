package ai.chat2db.server.tools.common.model.data.option.json;

import ai.chat2db.server.tools.common.model.data.option.BaseImportDataOptions;
import cn.hutool.core.date.DatePattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: zgq
 * @date: 2024年04月25日 23:08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ImportJsonDataOptions extends BaseImportDataOptions {
    private String rootNodeName;
    private String dataTimeFormat;

    public ImportJsonDataOptions() {
        dataTimeFormat = DatePattern.NORM_DATETIME_PATTERN;
        rootNodeName = "";
    }
}
