package ai.chat2db.server.tools.common.model.data.option;

import ai.chat2db.server.tools.common.model.data.option.json.ImportJsonDataOptions;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author: zgq
 * @date: 2024年04月25日 23:04
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "fileType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BaseImportExcelDataOptions.class, name = "CSV"),
        @JsonSubTypes.Type(value = BaseImportExcelDataOptions.class, name = "XLSX"),
        @JsonSubTypes.Type(value = BaseImportExcelDataOptions.class, name = "XLS"),
        @JsonSubTypes.Type(value = ImportJsonDataOptions.class, name = "JSON")
})
public abstract class AbstractImportDataOptions extends AbstractDataOption {

}
