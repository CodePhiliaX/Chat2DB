package ai.chat2db.server.web.api.controller.rdb.request;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author moji
 * @version TableUpdateDdlQueryRequest.java, v 0.1 September 16, 2022 14:23 moji Exp $
 * @date 2022/09/16
 */
@Data
public class TableUpdateDdlQueryRequest {

    /**
     * DB type
     * @see ai.chat2db.server.domain.support.enums.DbTypeEnum
     */
    @NotNull
    private String dbType;


}
