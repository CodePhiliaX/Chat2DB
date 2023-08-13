package ai.chat2db.server.domain.api.service;

import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.spi.model.Table;
import jakarta.validation.constraints.NotEmpty;

/**
 * author jipengfei
 * date 2021/9/23 15:22
 */
public interface ViewService {

    /**
     * Querying all views under a schema.
     *
     * @param databaseName
     * @return
     */
    ListResult<Table> views(@NotEmpty String databaseName, String schemaName);


    /**
     * Querying the details of a view.
     *
     * @param databaseName
     * @return
     */
    DataResult<Table> detail(@NotEmpty String databaseName, String schemaName,String tableName);
}
