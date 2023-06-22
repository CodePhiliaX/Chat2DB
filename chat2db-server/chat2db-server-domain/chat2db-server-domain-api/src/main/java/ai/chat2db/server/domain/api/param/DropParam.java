package ai.chat2db.server.domain.api.param;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 删除表结构
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DropParam {
    /**
     * 对应数据库存储的来源id
     */
    @NotNull
    private Long dataSourceId;

    /**
     * 对应的连接数据库名称
     */
    @NotNull
    private String databaseName;

    /**
     * 表名
     */
    private String tableName;

    /**
     *
     */
    private String tableSchema;
}
