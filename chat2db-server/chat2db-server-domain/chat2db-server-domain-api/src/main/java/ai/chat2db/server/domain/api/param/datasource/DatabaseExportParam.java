package ai.chat2db.server.domain.api.param.datasource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: zgq
 * @date: 2024年02月27日 22:08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DatabaseExportParam {
    /**
     * DB名称
     */
    private String databaseName;

    private String schemaName;

    private Boolean containData;

}
