package ai.chat2db.server.tools.common.model.rdb.data.option.table;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

/**
 * @author: zgq
 * @date: 2024年04月26日 9:39
 */

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "importType",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ImportTableOptions.class, name = "OLD"),
        @JsonSubTypes.Type(value = ImportNewTableOptions.class, name = "NEW"),
})
@Data
public class ImportTableOptions extends BaseTableOptions {
    private String importType;

    public ImportTableOptions() {
        importType = "OLD";
    }
}
