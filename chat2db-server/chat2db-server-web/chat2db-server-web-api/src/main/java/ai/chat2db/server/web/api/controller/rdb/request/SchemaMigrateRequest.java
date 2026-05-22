package ai.chat2db.server.web.api.controller.rdb.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SchemaMigrateRequest {

    @NotNull
    private Long targetDataSourceId;

    @NotNull
    private String targetDatabaseName;

    private String targetSchemaName;

    @NotNull
    private List<String> ddlStatements;

    private boolean executeInTransaction;

    private boolean continueOnError;
}
