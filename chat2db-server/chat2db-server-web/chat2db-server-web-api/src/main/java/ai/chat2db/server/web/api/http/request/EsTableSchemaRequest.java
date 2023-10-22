package ai.chat2db.server.web.api.http.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EsTableSchemaRequest {

    private Long dataSourceId;

    private String databaseName;

    private String apiKey;

    private String schemaName;

    private String tableName;

    private String tableSchemaContent;

    private String searchKey;

    private Boolean deleteBeforeInsert;
}
