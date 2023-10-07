package ai.chat2db.server.web.api.http.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TableSchema {

    private Long id;

    private Long dataSourceId;

    private String tableSchema;

    private String tableSchemaVector;

    private Integer wordCount;

    public TableSchema(Long id, Long dataSourceId, String tableSchema, Integer wordCount) {
        this.id = id;
        this.dataSourceId = dataSourceId;
        this.tableSchema = tableSchema;
        this.wordCount = wordCount;
    }
}
