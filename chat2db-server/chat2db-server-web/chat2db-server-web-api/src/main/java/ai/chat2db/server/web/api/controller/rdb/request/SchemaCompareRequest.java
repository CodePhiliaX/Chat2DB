package ai.chat2db.server.web.api.controller.rdb.request;

import java.util.List;

import ai.chat2db.server.domain.api.param.schemaDiff.CompareOption;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SchemaCompareRequest {

    @NotNull
    private Long sourceDataSourceId;

    @NotNull
    private String sourceDatabaseName;

    private String sourceSchemaName;

    @NotNull
    private Long targetDataSourceId;

    @NotNull
    private String targetDatabaseName;

    private String targetSchemaName;

    private List<String> tableNames;

    private CompareOption compareOption;
}
