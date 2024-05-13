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
     * Whitelist type, such as vector
     * @see ai.chat2db.server.tools.base.enums.WhiteListTypeEnum
     */
    private String whiteType;
}
