package ai.chat2db.server.domain.api.param;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 控制台创建参数
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ConsoleCreateParam {
    /**
     * 对应数据库存储的来源id
     */
    @NotNull
    private Long dataSourceId;

    /**
     * 控制台的id ，确保全局唯一
     * 确保不要重复，重复的情况下会弃用以前的连接，并重新创建
     */
    @NotNull
    private Long consoleId;

    /**
     * 对应的连接数据库名称
     * 支持多个database的数据库会调用use xx;来切换来数据库
     */
    @NotNull
    private String databaseName;
}
