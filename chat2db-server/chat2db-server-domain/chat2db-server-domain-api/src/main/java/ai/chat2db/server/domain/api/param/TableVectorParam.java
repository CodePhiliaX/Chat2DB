package ai.chat2db.server.domain.api.param;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TableVectorParam {

    /**
     * api key
     */
    @NotNull
    private String apiKey;

    /**
     * 数据源连接ID
     */
    private Long dataSourceId;

    /**
     * 数据库名称
     */
    private String database;

    /**
     * schema名称
     */
    private String schema;

    /**
     * 向量保存状态
     */
    private String status;
}
