package ai.chat2db.server.web.api.http.response;

import ai.chat2db.server.web.api.http.model.Knowledge;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeResponse {

    private List<Knowledge> knowledgeList;
}
