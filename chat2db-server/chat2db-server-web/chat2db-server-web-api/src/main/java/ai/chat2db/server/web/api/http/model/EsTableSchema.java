package ai.chat2db.server.web.api.http.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EsTableSchema {

    private String dataSourceId;

    private String databaseName;

    private String apiKey;

    private String schemaName;

    private String tableName;

    private String tableSchemaContent;
}
