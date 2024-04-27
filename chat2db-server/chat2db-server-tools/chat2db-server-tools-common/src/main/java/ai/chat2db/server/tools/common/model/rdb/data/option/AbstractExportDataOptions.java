package ai.chat2db.server.tools.common.model.rdb.data.option;

import ai.chat2db.server.tools.common.model.rdb.data.option.json.ExportData2JsonOptions;
import ai.chat2db.server.tools.common.model.rdb.data.option.sql.BaseExportData2SqlOptions;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author: zgq
 * @date: 2024年04月25日 23:31
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "fileType",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BaseExportDataOptions.class, name = "CSV"),
        @JsonSubTypes.Type(value = BaseExportData2SqlOptions.class, name = "SQL"),
        @JsonSubTypes.Type(value = BaseExportDataOptions.class, name = "XLSX"),
        @JsonSubTypes.Type(value = ExportData2JsonOptions.class, name = "JSON")
})
public abstract class AbstractExportDataOptions extends AbstractDataOption {

}
