package ai.chat2db.server.web.api.http.response;

import ai.chat2db.server.web.api.http.model.EsTableSchema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EsTableSchemaResponse {

    private List<EsTableSchema> tableSchemas;
}
