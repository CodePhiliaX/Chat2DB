package ai.chat2db.server.domain.api.service;

import ai.chat2db.server.domain.api.model.TreeNode;
import ai.chat2db.server.domain.api.param.TreeSearchParam;
import ai.chat2db.spi.model.Table;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

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
    List<Table> views(@NotEmpty String databaseName, String schemaName);

    /**
     * Querying all views under a schema with Lucene cache.
     *
     * @param dataSourceId data source id
     * @param databaseName database name
     * @param schemaName schema name
     * @param searchKey search keyword
     * @param refresh if true, refresh the cache
     * @return
     */
    List<Table> viewsWithCache(Long dataSourceId, String databaseName, String schemaName, String searchKey, boolean refresh);


    /**
     * Querying the details of a view.
     *
     * @param databaseName
     * @return
     */
    Table detail(@NotEmpty String databaseName, String schemaName,String tableName);

    /**
     * Search tree nodes for views.
     *
     * @param param
     * @return
     */
    List<TreeNode> searchTreeNodes(TreeSearchParam param);
}
