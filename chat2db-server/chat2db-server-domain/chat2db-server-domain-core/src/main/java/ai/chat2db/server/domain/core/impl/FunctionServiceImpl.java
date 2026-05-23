package ai.chat2db.server.domain.core.impl;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.chat2db.server.domain.api.model.TreeNode;
import ai.chat2db.server.domain.api.param.TreeSearchParam;
import ai.chat2db.server.domain.api.service.FunctionService;
import ai.chat2db.server.domain.core.cache.LuceneIndexManager;
import ai.chat2db.server.domain.core.cache.LuceneIndexManagerFactory;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.model.Function;
import ai.chat2db.spi.sql.Chat2DBContext;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FunctionServiceImpl implements FunctionService {

    @Autowired
    private LuceneIndexManagerFactory managerFactory;

    @Override
    public List<Function> functions(String databaseName, String schemaName) {
        return Chat2DBContext.getMetaData().functions(Chat2DBContext.getConnection(),databaseName, schemaName);
    }

    @Override
    public List<Function> functionsWithCache(Long dataSourceId, String databaseName, String schemaName, String searchKey, boolean refresh) {
        LuceneIndexManager<Function> mgr = managerFactory.getManager(dataSourceId);
        Function queryModel = Function.builder()
                .databaseName(databaseName)
                .schemaName(schemaName)
                .build();
        Long version = mgr.getMaxVersion(queryModel);

        if (refresh || version == null) {
            loadAndCacheMetadata(mgr, databaseName, schemaName, version);
        }

        List<Function> functions = mgr.search(queryModel, null, searchKey);
        return functions;
    }

    @Override
    public Function detail(String databaseName, String schemaName, String functionName) {
        return Chat2DBContext.getMetaData().function(Chat2DBContext.getConnection(), databaseName, schemaName, functionName);
    }

    @Override
    public List<TreeNode> searchTreeNodes(TreeSearchParam param) {
        LuceneIndexManager<Function> mgr = managerFactory.getManager(param.getDataSourceId());
        Function queryModel = Function.builder()
                .databaseName(param.getDatabaseName())
                .schemaName(param.getSchemaName())
                .build();
        Long version = mgr.getMaxVersion(queryModel);

        if (param.isRefresh() || version == null) {
            loadAndCacheMetadata(mgr, param.getDatabaseName(), param.getSchemaName(), version);
        }

        List<Function> functions = mgr.search(queryModel, null, param.getSearchKey());
        List<TreeNode> result = new ArrayList<>();
        for (Function function : functions) {
            TreeNode node = buildTreeNode(function);
            result.add(node);
        }
        return result;
    }

    private void loadAndCacheMetadata(LuceneIndexManager<Function> mgr, String databaseName, String schemaName, Long version) {
        mgr.getLock().writeLock().lock();
        try {
            Connection conn = Chat2DBContext.getConnection();
            MetaData meta = Chat2DBContext.getMetaData();
            List<Function> functions = meta.functions(conn, databaseName, schemaName);
            if (CollectionUtils.isEmpty(functions)) {
                mgr.deleteByDatabaseAndSchema(databaseName, schemaName);
                return;
            }
            mgr.updateDocuments(functions, version);
            log.info("[Function] Cached {} functions for database: {}", functions.size(), databaseName);
        } catch (Exception e) {
            log.error("[Function] loadAndCacheMetadata error", e);
        } finally {
            mgr.getLock().writeLock().unlock();
        }
    }

    private TreeNode buildTreeNode(Function function) {
        List<String> parentPath = new ArrayList<>();
        if (StringUtils.isNotBlank(function.getDatabaseName())) {
            parentPath.add(function.getDatabaseName());
        }
        if (StringUtils.isNotBlank(function.getSchemaName())) {
            parentPath.add(function.getSchemaName());
        }

        Map<String, Object> extraParams = new HashMap<>();
        extraParams.put("databaseName", function.getDatabaseName());
        extraParams.put("schemaName", function.getSchemaName());
        extraParams.put("functionName", function.getName());

        return TreeNode.builder()
                .uuid("function-" + function.getName())
                .key(function.getName())
                .name(function.getName())
                .treeNodeType("function")
                .comment(function.getComment())
                .isLeaf(true)
                .parentPath(parentPath)
                .extraParams(extraParams)
                .build();
    }
}

