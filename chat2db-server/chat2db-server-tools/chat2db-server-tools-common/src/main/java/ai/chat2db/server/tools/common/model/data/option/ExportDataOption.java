package ai.chat2db.server.tools.common.model.data.option;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "exportType",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ExportDataOption.class, name = "CSV"),
        @JsonSubTypes.Type(value = SQLExportDataOption.class, name = "SQL"),
        @JsonSubTypes.Type(value = ExportDataOption.class, name = "EXCEL"),
        @JsonSubTypes.Type(value = JSONExportDataOption.class, name = "JSON")
})
public class ExportDataOption {
    @NotBlank
    private String exportType;
    @NotNull
    public Boolean containsHeader;

}


