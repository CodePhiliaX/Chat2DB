package ai.chat2db.server.tools.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Configuration information for chat2db
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigJson {

    /**
     * Last successfully launched version
     */
    private String latestStartupSuccessVersion;
}
