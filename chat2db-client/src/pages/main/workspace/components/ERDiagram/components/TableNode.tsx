/**
 * ER图自定义节点组件
 * 用于显示数据库表的节点，包含表名、注释、列数量和字段列表
 */
import React, { memo } from 'react';
import { TableOutlined } from '@ant-design/icons';
import { Handle, NodeProps, Position } from '@xyflow/react';
import { Dropdown, MenuProps, Spin, Tooltip } from 'antd';
import i18n from '@/i18n';
import { IColumn, IErNode } from '@/service/sql';
import styles from './TableNode.less';

export interface IErDiagramFieldDragPayload {
  tableName: string;
  columnName: string;
}

/** 节点数据接口，扩展基础节点数据 */
export interface TableNodeData extends IErNode {
  /** 是否高亮显示（选中时） */
  isHighlighted?: boolean;
  /** 是否淡化显示（非选中关联表时） */
  isDimmed?: boolean;
  columns?: IColumn[];
  columnsExpanded?: boolean;
  columnsLoading?: boolean;
  virtualFkDragField?: IErDiagramFieldDragPayload | null;
  onCopyTableName?: (tableName: string) => void;
  onToggleColumns?: (tableName: string) => void;
  onCreateQuery?: (tableName: string) => void;
  onStartVirtualFkDrag?: (field: IErDiagramFieldDragPayload) => void;
  onFinishVirtualFkDrag?: (field: IErDiagramFieldDragPayload) => void;
}

const TableNode = memo(({ data }: NodeProps) => {
  const nodeData = data as unknown as TableNodeData;
  const menuItems: MenuProps['items'] = [
    {
      key: 'copy-table-name',
      label: i18n('workspace.erDiagram.copyTableName'),
      onClick: () => nodeData.onCopyTableName?.(nodeData.name),
    },
    {
      key: 'toggle-columns',
      label: nodeData.columnsExpanded
        ? i18n('workspace.erDiagram.collapseColumns')
        : i18n('workspace.erDiagram.expandColumns'),
      onClick: () => nodeData.onToggleColumns?.(nodeData.name),
    },
    {
      key: 'create-query',
      label: i18n('workspace.erDiagram.createJoinQuery'),
      onClick: () => nodeData.onCreateQuery?.(nodeData.name),
    },
  ];

  const renderColumns = () => {
    if (!nodeData.columnsExpanded) return null;

    if (nodeData.columnsLoading) {
      return (
        <div className={styles.columnLoading}>
          <Spin size="small" />
        </div>
      );
    }

    return (
      <div className={styles.columnList}>
        {(nodeData.columns || []).map((column) => {
          const isDragging =
            nodeData.virtualFkDragField?.tableName === nodeData.name &&
            nodeData.virtualFkDragField?.columnName === column.name;

          return (
            <div
              key={column.name}
              className={`${styles.columnItem} ${column.primaryKey ? styles.primaryColumn : ''} ${
                isDragging ? styles.draggingColumn : ''
              }`}
              onContextMenu={(event) => {
                event.preventDefault();
                event.stopPropagation();
              }}
              onMouseDown={(event) => {
                if (event.button !== 2) return;
                event.preventDefault();
                event.stopPropagation();
                nodeData.onStartVirtualFkDrag?.({ tableName: nodeData.name, columnName: column.name });
              }}
              onMouseUp={(event) => {
                if (event.button !== 2) return;
                event.preventDefault();
                event.stopPropagation();
                nodeData.onFinishVirtualFkDrag?.({ tableName: nodeData.name, columnName: column.name });
              }}
              title={`${nodeData.name}.${column.name}${column.columnType ? ` ${column.columnType}` : ''}`}
            >
              <span className={styles.columnName}>{column.name}</span>
              {column.primaryKey && <span className={styles.primaryKey}>PK</span>}
              {column.columnType && <span className={styles.columnType}>{column.columnType}</span>}
            </div>
          );
        })}
      </div>
    );
  };

  return (
    <Dropdown menu={{ items: menuItems }} trigger={['contextMenu']}>
      <div onContextMenu={(event) => event.stopPropagation()}>
        <Tooltip
          title={nodeData.comment ? `${nodeData.name}: ${nodeData.comment}` : nodeData.name}
          mouseEnterDelay={0.4}
        >
          <div
            className={`${styles.tableNode} ${nodeData.isHighlighted ? styles.highlighted : ''} ${
              nodeData.isDimmed ? styles.dimmed : ''
            }`}
          >
            <Handle type="target" position={Position.Left} className={styles.handle} />
            <Handle type="target" position={Position.Top} className={styles.handle} />
            <div className={styles.tableHeader}>
              <TableOutlined className={styles.tableIcon} />
              <span className={styles.tableName}>{nodeData.name}</span>
            </div>
            {(nodeData.comment || nodeData.columnCount != null || nodeData.columnsExpanded) && (
              <div className={styles.tableBody}>
                {nodeData.comment && <div className={styles.tableComment}>{nodeData.comment}</div>}
                {nodeData.columnCount != null && (
                  <div className={styles.columnCount}>{nodeData.columnCount} columns</div>
                )}
                {renderColumns()}
              </div>
            )}
            <Handle type="source" position={Position.Right} className={styles.handle} />
            <Handle type="source" position={Position.Bottom} className={styles.handle} />
          </div>
        </Tooltip>
      </div>
    </Dropdown>
  );
});

export default TableNode;
