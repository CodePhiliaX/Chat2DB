package ai.chat2db.server.domain.api.param.datasource;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 数据源关闭
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceCloseParam {
    /**
     * 对应数据库存储的来源id
     */
    @NotNull
    private Long dataSourceId;

}
