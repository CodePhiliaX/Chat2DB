package ai.chat2db.server.domain.api.service;

import ai.chat2db.server.domain.api.model.TreeNode;
import ai.chat2db.server.domain.api.param.TreeSearchParam;
import ai.chat2db.spi.model.Function;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * author jipengfei
 * date 2021/9/23 15:22
 */
public interface FunctionService {

    /**
     * Querying all functions under a schema.
     *
     * @param databaseName
     * @return
     */
    List<Function> functions(@NotEmpty String databaseName, String schemaName);

    /**
     * Querying all functions under a schema with Lucene cache.
     *
     * @param dataSourceId data source id
     * @param databaseName database name
     * @param schemaName schema name
     * @param searchKey search keyword
     * @param refresh if true, refresh the cache
     * @return
     */
    List<Function> functionsWithCache(Long dataSourceId, String databaseName, String schemaName, String searchKey, boolean refresh);

    /**
     * Querying function information.
     * @param databaseName
     * @param schemaName
     * @param functionName
     * @return
     */
    Function detail(String databaseName, String schemaName, String functionName);

    /**
     * Search tree nodes for functions.
     *
     * @param param
     * @return
     */
    List<TreeNode> searchTreeNodes(TreeSearchParam param);
}
