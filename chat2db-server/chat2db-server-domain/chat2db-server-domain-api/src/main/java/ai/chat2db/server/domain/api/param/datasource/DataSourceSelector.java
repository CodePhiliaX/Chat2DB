package ai.chat2db.server.domain.api.param.datasource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author moji
 * @version DataSourceSelector.java, v 0.1 September 23, 2022 15:28 moji Exp $
 * @date 2022/09/23
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceSelector {

    /**
     * environment id
     */
    private Boolean environment;
}
