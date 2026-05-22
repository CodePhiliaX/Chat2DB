package ai.chat2db.server.domain.api.param.schemaDiff;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SchemaCompareParam {
    private Long sourceDataSourceId;
    private String sourceDatabaseName;
    private String sourceSchemaName;
    private Long targetDataSourceId;
    private String targetDatabaseName;
    private String targetSchemaName;
    private List<String> tableNames;
    private CompareOption compareOption;
}
