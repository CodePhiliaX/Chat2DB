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
     * 授权对象
     */
    private Boolean accessObject;

    /**
     * 数据源
     */
    private Boolean dataSource;

    /**
     * 数据源
     */
    private DataSourceSelector dataSourceSelector;
}
