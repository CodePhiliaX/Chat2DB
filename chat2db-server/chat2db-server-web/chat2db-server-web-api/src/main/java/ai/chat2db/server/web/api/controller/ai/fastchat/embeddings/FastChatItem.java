package ai.chat2db.server.web.api.controller.ai.fastchat.embeddings;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class FastChatItem implements Serializable {
    private String object;
    private List<BigDecimal> embedding;
    private Integer index;
}
