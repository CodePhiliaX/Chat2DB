package ai.chat2db.server.domain.api.param.datasource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author moji
 * @version DataSourceSelector.java, v 0.1 2022年09月23日 15:28 moji Exp $
 * @date 2022/09/23
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceSelector {

    /**
     * 环境id
     */
    private Boolean environment;
}
