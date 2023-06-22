package ai.chat2db.server.web.api.controller.rdb.request;

import jakarta.validation.constraints.NotNull;

import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;

import lombok.Data;

/**
 * 修改表sql请求
 *
 * @author 是仪
 */
@Data
public class TableModifySqlRequest extends DataSourceBaseRequest {

    /**
     * 旧的表结构
     * 为空代表新建表
     */
    private TableRequest oldTable;

    /**
     * 新的表结构
     */
    @NotNull
    private TableRequest newTable;

}
