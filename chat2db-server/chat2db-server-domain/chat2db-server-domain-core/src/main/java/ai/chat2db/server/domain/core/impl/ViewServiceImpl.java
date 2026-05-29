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
import ai.chat2db.server.domain.api.service.ViewService;
import ai.chat2db.server.domain.core.cache.LuceneIndexManager;
import ai.chat2db.server.domain.core.cache.LuceneIndexManagerFactory;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.model.View;
import ai.chat2db.spi.sql.Chat2DBContext;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ViewServiceImpl implements ViewService {

    @Autowired
    private LuceneIndexManagerFactory managerFactory;

    @Override
    public List<Table> views(String databaseName, String schemaName) {
        return Chat2DBContext.getMetaData().views(Chat2DBContext.getConnection(),databaseName, schemaName);
    }

    @Override
    public List<Table> viewsWithCache(Long dataSourceId, String databaseName, String schemaName, String searchKey, boolean refresh) {
        LuceneIndexManager<View> mgr = managerFactory.getManager(dataSourceId);
        View queryModel = View.builder()
                .databaseName(databaseName)
                .schemaName(schemaName)
                .build();
        Long version = mgr.getMaxVersion(queryModel);

        if (refresh || version == null) {
            loadAndCacheMetadata(mgr, databaseName, schemaName, version);
        }

        return new ArrayList<>(mgr.search(queryModel, null, searchKey));
    }

    @Override
    public Table detail(String databaseName, String schemaName, String tableName) {
        MetaData metaSchema = Chat2DBContext.getMetaData();
        Table table = metaSchema.view(Chat2DBContext.getConnection(), databaseName, schemaName, tableName);
        return table;
    }

    @Override
    public List<TreeNode> searchTreeNodes(TreeSearchParam param) {
        LuceneIndexManager<View> mgr = managerFactory.getManager(param.getDataSourceId());
        View queryModel = View.builder()
                .databaseName(param.getDatabaseName())
                .schemaName(param.getSchemaName())
                .build();
        Long version = mgr.getMaxVersion(queryModel);

        if (param.isRefresh() || version == null) {
            loadAndCacheMetadata(mgr, param.getDatabaseName(), param.getSchemaName(), version);
        }

        List<View> views = mgr.search(queryModel, null, param.getSearchKey());
        List<TreeNode> result = new ArrayList<>();
        for (Table view : views) {
            TreeNode node = buildTreeNode(view);
            result.add(node);
        }
        return result;
    }

    private void loadAndCacheMetadata(LuceneIndexManager<View> mgr, String databaseName, String schemaName, Long currentVersion) {
        mgr.getLock().writeLock().lock();
        try {
            Connection conn = Chat2DBContext.getConnection();
            MetaData meta = Chat2DBContext.getMetaData();
            List<View> views = meta.views(conn, databaseName, schemaName).stream()
                    .map(View::from)
                    .toList();
            if (CollectionUtils.isEmpty(views)) {
                mgr.deleteByDatabaseAndSchema(View.builder()
                        .databaseName(databaseName)
                        .schemaName(schemaName)
                        .build());
                return;
            }
            mgr.updateDocuments(views, currentVersion);
            log.info("[View] Cached {} views for database: {}", views.size(), databaseName);
        } catch (Exception e) {
            log.error("[View] loadAndCacheMetadata error", e);
        } finally {
            mgr.getLock().writeLock().unlock();
        }
    }

    private TreeNode buildTreeNode(Table view) {
        List<String> parentPath = new ArrayList<>();
        if (StringUtils.isNotBlank(view.getDatabaseName())) {
            parentPath.add(view.getDatabaseName());
        }
        if (StringUtils.isNotBlank(view.getSchemaName())) {
            parentPath.add(view.getSchemaName());
        }

        Map<String, Object> extraParams = new HashMap<>();
        extraParams.put("databaseName", view.getDatabaseName());
        extraParams.put("schemaName", view.getSchemaName());
        extraParams.put("tableName", view.getName());

        return TreeNode.builder()
                .uuid("view-" + view.getName())
                .key(view.getName())
                .name(view.getName())
                .treeNodeType("view")
                .comment(view.getComment())
                .isLeaf(true)
                .parentPath(parentPath)
                .extraParams(extraParams)
                .build();
    }
}
