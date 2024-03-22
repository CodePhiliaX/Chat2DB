package ai.chat2db.server.web.api.controller.rdb.request;

import ai.chat2db.server.domain.api.enums.ExportSizeEnum;
import ai.chat2db.server.domain.api.enums.ExportTypeEnum;
import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author moji
 * @version ConnectionQueryRequest.java, v 0.1 2022年09月16日 14:23 moji Exp $
 * @date 2022/09/16
 */
@Data
public class DataExportRequest extends DataSourceBaseRequest {
    /**
     * Executed SQL
     */
    private String sql;

    /**
     * Original SQL without pagination
     */
    private String originalSql;

    /**
     * export type
     *
     * @see ExportTypeEnum
     */
    @NotNull
    private String exportType;

    /**
     * How much data is currently needed at the beginning
     *
     * @see ExportSizeEnum
     */
    @NotNull
    private String exportSize;
}
