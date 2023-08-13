package ai.chat2db.server.domain.api.service;

import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.spi.model.Trigger;
import jakarta.validation.constraints.NotEmpty;

public interface TriggerService {

    /**
     * Querying all triggers under a schema.
     *
     * @param databaseName
     * @return
     */
    ListResult<Trigger> triggers(@NotEmpty String databaseName, String schemaName);

    /**
     * Querying trigger information.
     * @param databaseName
     * @param schemaName
     * @param triggerName
     * @return
     */
    DataResult<Trigger> detail(String databaseName, String schemaName, String triggerName);
}
