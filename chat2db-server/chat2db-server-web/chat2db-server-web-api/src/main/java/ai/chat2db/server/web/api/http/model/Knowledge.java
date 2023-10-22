package ai.chat2db.server.web.api.http.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Knowledge {

    private Long id;

    private String content;

    private String contentVector;

    private Integer wordCount;

    public Knowledge(Long id, String content, Integer wordCount) {
        this.id = id;
        this.content = content;
        this.wordCount = wordCount;
    }
}
