package ai.chat2db.server.web.api.http.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class WhiteListRequest {

    /**
     * api key
     */
    private String apiKey;

    /**
     * 白名单类型，如向量
     * @see ai.chat2db.server.tools.base.enums.WhiteListTypeEnum
     */
    private String whiteType;
}
