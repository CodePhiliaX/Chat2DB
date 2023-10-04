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

    private List<List<BigDecimal>> contentVector;

    private List<String> sentenceList;
}
