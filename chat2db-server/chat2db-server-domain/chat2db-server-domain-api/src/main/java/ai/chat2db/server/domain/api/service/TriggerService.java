package ai.chat2db.server.domain.api.service;

import ai.chat2db.server.domain.api.model.TreeNode;
import ai.chat2db.server.domain.api.param.TreeSearchParam;
import ai.chat2db.spi.model.Trigger;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public interface TriggerService {

    /**
     * Querying all triggers under a schema.
     *
     * @param databaseName
     * @return
     */
    List<Trigger> triggers(@NotEmpty String databaseName, String schemaName);

    /**
     * Querying all triggers under a schema with Lucene cache.
     *
     * @param dataSourceId data source id
     * @param databaseName database name
     * @param schemaName schema name
     * @param searchKey search keyword
     * @param refresh if true, refresh the cache
     * @return
     */
    List<Trigger> triggersWithCache(Long dataSourceId, String databaseName, String schemaName, String searchKey, boolean refresh);

    /**
     * Querying trigger information.
     * @param databaseName
     * @param schemaName
     * @param triggerName
     * @return
     */
    Trigger detail(String databaseName, String schemaName, String triggerName);

    /**
     * Search tree nodes for triggers.
     *
     * @param param
     * @return
     */
    List<TreeNode> searchTreeNodes(TreeSearchParam param);
}
