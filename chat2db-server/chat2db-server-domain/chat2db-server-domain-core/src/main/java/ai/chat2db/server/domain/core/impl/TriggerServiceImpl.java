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
import ai.chat2db.server.domain.api.service.TriggerService;
import ai.chat2db.server.domain.core.cache.LuceneIndexManager;
import ai.chat2db.server.domain.core.cache.LuceneIndexManagerFactory;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.model.Trigger;
import ai.chat2db.spi.sql.Chat2DBContext;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TriggerServiceImpl implements TriggerService {

    @Autowired
    private LuceneIndexManagerFactory managerFactory;

    @Override
    public List<Trigger> triggers(String databaseName, String schemaName) {
        return Chat2DBContext.getMetaData().triggers(Chat2DBContext.getConnection(),databaseName, schemaName);
    }

    @Override
    public List<Trigger> triggersWithCache(Long dataSourceId, String databaseName, String schemaName, String searchKey, boolean refresh) {
        LuceneIndexManager<Trigger> mgr = managerFactory.getManager(dataSourceId);
        Trigger queryModel = Trigger.builder()
                .databaseName(databaseName)
                .schemaName(schemaName)
                .build();
        Long version = mgr.getMaxVersion(queryModel);

        if (refresh || version == null) {
            loadAndCacheMetadata(mgr, databaseName, schemaName, version);
        }

        return mgr.search(queryModel, null, searchKey);
    }

    @Override
    public Trigger detail(String databaseName, String schemaName, String triggerName) {
        return Chat2DBContext.getMetaData().trigger(Chat2DBContext.getConnection(), databaseName, schemaName, triggerName);
    }

    @Override
    public List<TreeNode> searchTreeNodes(TreeSearchParam param) {
        LuceneIndexManager<Trigger> mgr = managerFactory.getManager(param.getDataSourceId());
        Trigger queryModel = Trigger.builder()
                .databaseName(param.getDatabaseName())
                .schemaName(param.getSchemaName())
                .build();
        Long version = mgr.getMaxVersion(queryModel);

        if (param.isRefresh() || version == null) {
            loadAndCacheMetadata(mgr, param.getDatabaseName(), param.getSchemaName(), version);
        }

        List<Trigger> triggers = mgr.search(queryModel, null, param.getSearchKey());
        List<TreeNode> result = new ArrayList<>();
        for (Trigger trigger : triggers) {
            TreeNode node = buildTreeNode(trigger);
            result.add(node);
        }
        return result;
    }

    private void loadAndCacheMetadata(LuceneIndexManager<Trigger> mgr, String databaseName, String schemaName, Long currentVersion) {
        mgr.getLock().writeLock().lock();
        try {
            Connection conn = Chat2DBContext.getConnection();
            MetaData meta = Chat2DBContext.getMetaData();
            List<Trigger> triggers = meta.triggers(conn, databaseName, schemaName);
            if (CollectionUtils.isEmpty(triggers)) {
                return;
            }
            mgr.updateDocuments(triggers, currentVersion);
            log.info("[Trigger] Cached {} triggers for database: {}", triggers.size(), databaseName);
        } catch (Exception e) {
            log.error("[Trigger] loadAndCacheMetadata error", e);
        } finally {
            mgr.getLock().writeLock().unlock();
        }
    }

    private TreeNode buildTreeNode(Trigger trigger) {
        List<String> parentPath = new ArrayList<>();
        if (StringUtils.isNotBlank(trigger.getDatabaseName())) {
            parentPath.add(trigger.getDatabaseName());
        }
        if (StringUtils.isNotBlank(trigger.getSchemaName())) {
            parentPath.add(trigger.getSchemaName());
        }

        Map<String, Object> extraParams = new HashMap<>();
        extraParams.put("databaseName", trigger.getDatabaseName());
        extraParams.put("schemaName", trigger.getSchemaName());
        extraParams.put("triggerName", trigger.getName());

        return TreeNode.builder()
                .uuid("trigger-" + trigger.getName())
                .key(trigger.getName())
                .name(trigger.getName())
                .treeNodeType("trigger")
                .comment(trigger.getComment())
                .isLeaf(true)
                .parentPath(parentPath)
                .extraParams(extraParams)
                .build();
    }
}