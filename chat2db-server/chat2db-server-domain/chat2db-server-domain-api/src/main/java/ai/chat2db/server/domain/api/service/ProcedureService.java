package ai.chat2db.server.domain.api.service;

import ai.chat2db.server.domain.api.model.TreeNode;
import ai.chat2db.server.domain.api.param.TreeSearchParam;
import ai.chat2db.spi.model.Procedure;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public interface ProcedureService {

    /**
     * Querying all procedures under a schema.
     *
     * @param databaseName
     * @return
     */
    List<Procedure> procedures(@NotEmpty String databaseName, String schemaName);

    /**
     * Querying all procedures under a schema with Lucene cache.
     *
     * @param dataSourceId data source id
     * @param databaseName database name
     * @param schemaName schema name
     * @param searchKey search keyword
     * @param refresh if true, refresh the cache
     * @return
     */
    List<Procedure> proceduresWithCache(Long dataSourceId, String databaseName, String schemaName, String searchKey, boolean refresh);

    /**
     * Querying procedure information.
     * @param databaseName
     * @param schemaName
     * @param procedureName
     * @return
     */
    Procedure detail(String databaseName, String schemaName, String procedureName);

    /**
     * Search tree nodes for procedures.
     *
     * @param param
     * @return
     */
    List<TreeNode> searchTreeNodes(TreeSearchParam param);
}
