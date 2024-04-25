package ai.chat2db.server.tools.common.model.data.option;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: zgq
 * @date: 2024年04月23日 13:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "importType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CSVImportDataOption.class, name = "CSV"),
        @JsonSubTypes.Type(value = ImportDataOption.class, name = "SQL"),
        @JsonSubTypes.Type(value = CSVImportDataOption.class, name = "EXCEL"),
        @JsonSubTypes.Type(value = JSONImportDataOption.class, name = "JSON")
})
public class ImportDataOption {
    @NotBlank
    private String importType;
}
