package ai.chat2db.server.domain.core.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.chat2db.server.domain.api.param.CreateVirtualFKParam;
import ai.chat2db.server.domain.api.param.ErDiagramQueryParam;
import ai.chat2db.server.domain.api.param.TablePageQueryParam;
import ai.chat2db.server.domain.api.param.TableQueryParam;
import ai.chat2db.server.domain.api.param.TableSelector;
import ai.chat2db.server.domain.api.service.ErDiagramService;
import ai.chat2db.server.domain.api.service.ForeignKeySyncService;
import ai.chat2db.server.domain.api.service.TableService;
import ai.chat2db.server.domain.api.vo.InferVirtualFkResultVO;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import ai.chat2db.spi.model.ErDiagram;
import ai.chat2db.spi.model.ForeignKey;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.model.TableColumn;
import ai.chat2db.spi.model.TableIndex;
import ai.chat2db.spi.model.TableIndexColumn;
import ai.chat2db.spi.model.VirtualForeignKey;
import ai.chat2db.spi.model.SimpleTable;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ErDiagramServiceImpl implements ErDiagramService {

    @Autowired
    private TableService tableService;

    @Autowired
    private ForeignKeySyncService foreignKeySyncService;

    @Override
    public ErDiagram queryErDiagram(ErDiagramQueryParam param) {
        if (Boolean.TRUE.equals(param.getSyncForeignKeys())) {
            foreignKeySyncService.syncForeignKeys(
                    param.getDataSourceId(),
                    param.getDatabaseName(),
                    param.getSchemaName(),
                    null
            );
        }
        
        List<Table> tables = queryTables(param);
        List<ErDiagram.Node> nodes = buildNodes(tables);
        Set<String> tableNameSet = tables.stream().map(Table::getName).collect(Collectors.toSet());
        boolean includeVirtual = param.getIncludeVirtualFk() == null || param.getIncludeVirtualFk();
        List<ErDiagram.Edge> edges = buildEdges(tables, tableNameSet, includeVirtual);
        
        if (Boolean.TRUE.equals(param.getOnlyRelatedTables())) {
            Set<String> relatedTableIds = edges.stream()
                    .flatMap(e -> java.util.stream.Stream.of(e.getSource(), e.getTarget()))
                    .collect(Collectors.toSet());
            nodes = nodes.stream()
                    .filter(n -> relatedTableIds.contains(n.getId()))
                    .collect(Collectors.toList());
            edges = edges.stream()
                    .filter(e -> relatedTableIds.contains(e.getSource()) && relatedTableIds.contains(e.getTarget()))
                    .collect(Collectors.toList());
        }
        
        return ErDiagram.builder().nodes(nodes).edges(edges).build();
    }

    private List<Table> queryTables(ErDiagramQueryParam param) {
        TablePageQueryParam tablePageQueryParam = TablePageQueryParam.builder()
                .dataSourceId(param.getDataSourceId())
                .databaseName(param.getDatabaseName())
                .schemaName(param.getSchemaName())
                .searchKey(param.getTableNameFilter())
                .build();
        TableSelector selector = new TableSelector();
        selector.setColumnList(true);
        selector.setIndexList(true);
        selector.setForeignKey(true);
        return tableService.pageQuery(tablePageQueryParam, selector).getData();
    }

    private List<ErDiagram.Node> buildNodes(List<Table> tables) {
        return tables.stream().map(table -> ErDiagram.Node.builder()
                .id(table.getName())
                .name(table.getName())
                .comment(table.getComment())
                .columnCount(CollectionUtils.isEmpty(table.getColumnList()) ? 0 : table.getColumnList().size())
                .build()
        ).collect(Collectors.toList());
    }

    private List<ErDiagram.Edge> buildEdges(List<Table> tables, Set<String> tableNameSet, boolean includeVirtual) {
        List<ErDiagram.Edge> edges = new ArrayList<>();
        for (Table table : tables) {
            if (CollectionUtils.isNotEmpty(table.getForeignKeyList())) {
                for (ForeignKey fk : table.getForeignKeyList()) {
                    if (tableNameSet.contains(fk.getReferencedTable())) {
                        edges.add(buildEdge(table.getName(), fk, false));
                    }
                }
            }
            if (includeVirtual && CollectionUtils.isNotEmpty(table.getVirtualForeignKeyList())) {
                for (VirtualForeignKey vfk : table.getVirtualForeignKeyList()) {
                    if (tableNameSet.contains(vfk.getReferencedTable())) {
                        edges.add(buildEdge(table.getName(), vfk, true));
                    }
                }
            }
        }
        return edges;
    }

    private ErDiagram.Edge buildEdge(String tableName, ForeignKey fk, boolean virtual) {
        String defaultId = virtual
                ? "VFK_" + tableName + "_" + fk.getColumn()
                : tableName + "_" + fk.getColumn() + "_" + fk.getReferencedTable();
        return ErDiagram.Edge.builder()
                .id(fk.getName() != null ? fk.getName() : defaultId)
                .source(tableName)
                .target(fk.getReferencedTable())
                .sourceColumn(fk.getColumn())
                .targetColumn(fk.getReferencedColumn())
                .label(fk.getColumn() + " -> " + fk.getReferencedColumn())
                .virtual(virtual)
                .build();
    }

    @Override
    public InferVirtualFkResultVO inferVirtualForeignKeys(ErDiagramQueryParam param) {
        List<VirtualForeignKey> beforeInfer = foreignKeySyncService.queryAllVirtualForeignKeys(
                param.getDataSourceId(),
                param.getDatabaseName(),
                param.getSchemaName()
        );

        List<Table> tables = queryTables(param);
        
        List<String> existingTableNames = tables.stream()
                .map(Table::getName)
                .collect(Collectors.toList());
        
        int cleanedCount = foreignKeySyncService.cleanInvalidVirtualForeignKeys(
                param.getDataSourceId(),
                param.getDatabaseName(),
                param.getSchemaName(),
                existingTableNames
        );
        log.info("Cleaned {} invalid virtual foreign keys before inference", cleanedCount);

        List<VirtualForeignKey> addedList = new ArrayList<>();
        for (Table table : tables) {
            List<VirtualForeignKey> inferredFKs = findVirtualForeignKeys(table, param);
            for (VirtualForeignKey vfk : inferredFKs) {
                try {
                    foreignKeySyncService.createVirtualFK(
                            CreateVirtualFKParam.builder()
                                    .dataSourceId(param.getDataSourceId())
                                    .databaseName(param.getDatabaseName())
                                    .schemaName(param.getSchemaName())
                                    .tableName(table.getName())
                                    .columnName(vfk.getColumn())
                                    .referencedTable(vfk.getReferencedTable())
                                    .referencedColumnName(vfk.getReferencedColumn())
                                    .comment("Inferred from column naming convention")
                                    .sourceType("INFERRED")
                                    .build()
                    );
                    addedList.add(vfk);
                } catch (Exception e) {
                    log.warn("Failed to create inferred virtual FK for {}.{} -> {}.{}",
                            table.getName(), vfk.getColumn(),
                            vfk.getReferencedTable(), vfk.getReferencedColumn(), e);
                }
            }
        }

        List<VirtualForeignKey> afterInfer = foreignKeySyncService.queryAllVirtualForeignKeys(
                param.getDataSourceId(),
                param.getDatabaseName(),
                param.getSchemaName()
        );

        Set<String> beforeKeys = beforeInfer.stream()
                .map(vfk -> vfk.getTableName() + "." + vfk.getColumn())
                .collect(Collectors.toSet());
        Set<String> afterKeys = afterInfer.stream()
                .map(vfk -> vfk.getTableName() + "." + vfk.getColumn())
                .collect(Collectors.toSet());

        List<InferVirtualFkResultVO.VirtualFkItem> deletedItems = beforeInfer.stream()
                .filter(vfk -> !afterKeys.contains(vfk.getTableName() + "." + vfk.getColumn()))
                .map(vfk -> InferVirtualFkResultVO.VirtualFkItem.builder()
                        .tableName(vfk.getTableName())
                        .columnName(vfk.getColumn())
                        .referencedTable(vfk.getReferencedTable())
                        .referencedColumnName(vfk.getReferencedColumn())
                        .build())
                .collect(Collectors.toList());

        List<InferVirtualFkResultVO.VirtualFkItem> addedItems = addedList.stream()
                .map(vfk -> InferVirtualFkResultVO.VirtualFkItem.builder()
                        .tableName(vfk.getTableName())
                        .columnName(vfk.getColumn())
                        .referencedTable(vfk.getReferencedTable())
                        .referencedColumnName(vfk.getReferencedColumn())
                        .build())
                .collect(Collectors.toList());

        InferVirtualFkResultVO result = InferVirtualFkResultVO.builder()
                .addedCount(addedItems.size())
                .deletedCount(deletedItems.size())
                .added(addedItems)
                .deleted(deletedItems)
                .build();

        return result;
    }

    /**
     * 发现可能的虚拟外键关系（根据命名规范推断）
     * 参考 TableServiceImpl.findVirtualForeignKeys 实现
     */
    private List<VirtualForeignKey> findVirtualForeignKeys(Table table, ErDiagramQueryParam param) {
        List<VirtualForeignKey> result = new ArrayList<>();

        // 预加载已明确声明的外键列名（用于排除已存在的外键）
        Set<String> explicitForeignKeys = table.getForeignKeyList().stream()
                .map(ForeignKey::getColumn)
                .collect(Collectors.toCollection(java.util.LinkedHashSet::new));

        // 排除唯一索引列
        if (CollectionUtils.isNotEmpty(table.getIndexList())) {
            table.getIndexList().stream()
                    .filter(index -> Boolean.TRUE.equals(index.getUnique()))
                    .map(TableIndex::getColumnList)
                    .flatMap(List::stream)
                    .map(TableIndexColumn::getColumnName)
                    .forEach(explicitForeignKeys::add);
        }

        // 查询已存储的虚拟外键，避免重复推断
        List<VirtualForeignKey> storedVirtualFKs = foreignKeySyncService.queryVirtualForeignKeys(
                param.getDataSourceId(),
                param.getDatabaseName(),
                param.getSchemaName(),
                table.getName()
        );
        Set<String> existingVirtualFKColumns = storedVirtualFKs.stream()
                .map(VirtualForeignKey::getColumn)
                .collect(Collectors.toSet());

        // 筛选候选列
        if (CollectionUtils.isNotEmpty(table.getColumnList())) {
            for (TableColumn column : table.getColumnList()) {
                if (isPotentialVirtualKeyCandidate(column)
                        && !explicitForeignKeys.contains(column.getName())
                        && !existingVirtualFKColumns.contains(column.getName())) {
                    VirtualForeignKey vfk = analyzeColumnRelation(table, column, param);
                    if (vfk != null) {
                        result.add(vfk);
                    }
                }
            }
        }

        return result;
    }

    /**
     * 判断列是否为虚拟外键候选列
     */
    private boolean isPotentialVirtualKeyCandidate(TableColumn column) {
        return column.getName() != null
                && column.getName().endsWith("_id")
                && Boolean.FALSE.equals(column.getPrimaryKey())
                && column.getName().length() > 3;
    }

    /**
     * 分析列关联关系并构建虚拟外键
     */
    private VirtualForeignKey analyzeColumnRelation(Table currentTable, TableColumn currentColumn, ErDiagramQueryParam param) {
        String columnName = currentColumn.getName();
        String referencedTableName = columnName.substring(0, columnName.length() - 3);
        String currentTableName = currentTable.getName();

        // 排除自关联
        if (referencedTableName.equalsIgnoreCase(currentTableName)) {
            return null;
        }

        // 查找匹配的表
        TableSelector selector = new TableSelector();
        selector.setColumnList(true);
        List<Table> tables = tableService.pageQuery(
                TablePageQueryParam.builder()
                        .dataSourceId(param.getDataSourceId())
                        .databaseName(currentColumn.getDatabaseName())
                        .schemaName(currentColumn.getSchemaName())
                        .searchKey(referencedTableName)
                        .build(),
                selector
        ).getData();

        // 排除自关联
        Table targetTable = tables.stream()
                .filter(t -> !currentTableName.equalsIgnoreCase(t.getName()))
                .findFirst()
                .orElse(null);

        if (targetTable == null) {
            return null;
        }

        // 查找目标表的关联列
        String referencedColumnName = "id";
        if (CollectionUtils.isNotEmpty(targetTable.getColumnList())) {
            for (TableColumn tableColumn : targetTable.getColumnList()) {
                if (columnName.equalsIgnoreCase(tableColumn.getName())) {
                    referencedColumnName = tableColumn.getName();
                    break;
                } else if ("id".equals(referencedColumnName) && Boolean.TRUE.equals(tableColumn.getPrimaryKey())) {
                    referencedColumnName = tableColumn.getName();
                }
            }
        }

        return VirtualForeignKey.builder()
                .name(String.format("VFK_%s_%s", currentTableName, columnName))
                .tableName(currentTableName)
                .column(columnName)
                .referencedTable(targetTable.getName())
                .referencedColumn(referencedColumnName)
                .virtualProperty("Inferred from column naming convention")
                .build();
    }
}