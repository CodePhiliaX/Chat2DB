package ai.chat2db.server.domain.api.param.datasource.access;

import ai.chat2db.server.domain.api.param.datasource.DataSourceSelector;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * slecetor
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceAccessSelector {

    /**
     * Authorization object
     */
    private Boolean accessObject;

    /**
     * data source
     */
    private Boolean dataSource;

    /**
     * data source
     */
    private DataSourceSelector dataSourceSelector;
}
