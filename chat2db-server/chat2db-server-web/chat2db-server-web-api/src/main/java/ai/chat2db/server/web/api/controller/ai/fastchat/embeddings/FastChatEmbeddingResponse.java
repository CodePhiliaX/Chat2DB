package ai.chat2db.server.web.api.controller.ai.fastchat.embeddings;

import com.unfbx.chatgpt.entity.common.Usage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 描述：
 *
 * @author https:www.unfbx.com
 *  2023-02-15
 */
@Data
public class FastChatEmbeddingResponse implements Serializable {

    private String object;
    private List<FastChatItem> data;
    private String model;
    private Usage usage;
}
