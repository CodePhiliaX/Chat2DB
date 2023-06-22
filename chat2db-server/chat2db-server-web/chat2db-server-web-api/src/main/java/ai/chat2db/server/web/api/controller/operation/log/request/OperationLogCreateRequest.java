package ai.chat2db.server.web.api.controller.operation.log.request;

import javax.validation.constraints.NotNull;

import ai.chat2db.server.domain.support.enums.DbTypeEnum;
import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;

import lombok.Data;

/**
 * @author moji
 * @version DdlCreateRequest.java, v 0.1 2022年09月18日 11:13 moji Exp $
 * @date 2022/09/18
 */
@Data
public class OperationLogCreateRequest extends DataSourceBaseRequest {

    /**
     * 文件别名
     */
    private String name;

    /**
     * ddl类型
     * @see DbTypeEnum
     */
    @NotNull
    private String type;

    /**
     * ddl内容
     */
    @NotNull
    private String ddl;
}
