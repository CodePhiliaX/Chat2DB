package ai.chat2db.server.domain.api.param;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * 统计数量
 *
 * @author 是仪
 */
@Data
public class DlCountParam {

    /**
     * sql语句
     */
    @NotNull
    private String sql;

    /**
     * 控制台id
     */
    @NotNull
    private Long consoleId;

    /**
     * 数据源id
     */
    @NotNull
    private Long dataSourceId;

    /**
     * DB名称
     */
    @NotNull
    private String databaseName;
}
