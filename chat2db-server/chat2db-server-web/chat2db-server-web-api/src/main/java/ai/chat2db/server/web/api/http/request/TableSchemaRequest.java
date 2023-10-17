package ai.chat2db.server.web.api.http.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TableSchemaRequest {

    private Long dataSourceId;

    private String databaseName;

    private String apiKey;

    private String dataSourceSchema;

    private List<java.util.List<BigDecimal>> schemaVector;

    private List<String> schemaList;

    private Boolean deleteBeforeInsert = false;
}
