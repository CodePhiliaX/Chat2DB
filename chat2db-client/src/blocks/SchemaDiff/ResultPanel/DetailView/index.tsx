import React, { memo, useState } from 'react';
import { Tabs, Tag, Table, Tooltip } from 'antd';
import classnames from 'classnames';

import { i18n } from '@/i18n';
import { ITableDiff, IColumnDiff, IIndexDiff, IForeignKeyDiff } from '@/typings/schemaDiff';
import styles from './index.less';

interface DetailViewProps {
  tableDiff: ITableDiff;
}

const changeTypeColor: Record<string, string> = {
  ADD: '#52c41a',
  MODIFY: '#faad14',
  DELETE: '#ff4d4f',
};

const changeTypeLabel: Record<string, string> = {
  ADD: i18n('schemaDiff.added'),
  MODIFY: i18n('schemaDiff.modified'),
  DELETE: i18n('schemaDiff.removed'),
};

const columnColumns = [
  {
    title: i18n('schemaDiff.operation'),
    dataIndex: 'changeType',
    key: 'changeType',
    width: 72,
    render: (v: string) => <Tag color={changeTypeColor[v]}>{changeTypeLabel[v]}</Tag>,
  },
  { title: i18n('schemaDiff.columnName'), dataIndex: 'name', key: 'name', width: 140 },
  { title: i18n('schemaDiff.columnType'), dataIndex: 'columnType', key: 'columnType', width: 120 },
  { title: 'Size', dataIndex: 'size', key: 'size', width: 60 },
  { title: i18n('schemaDiff.nullable'), dataIndex: 'nullable', key: 'nullable', width: 60, render: (v: any) => v ? 'YES' : 'NO' },
  { title: i18n('schemaDiff.defaultValue'), dataIndex: 'defaultValue', key: 'defaultValue', width: 100 },
  { title: i18n('schemaDiff.comment'), dataIndex: 'comment', key: 'comment', ellipsis: true },
];

const indexColumns = [
  {
    title: i18n('schemaDiff.operation'),
    dataIndex: 'changeType',
    key: 'changeType',
    width: 72,
    render: (v: string) => <Tag color={changeTypeColor[v]}>{changeTypeLabel[v]}</Tag>,
  },
  { title: i18n('schemaDiff.indexName'), dataIndex: 'name', key: 'name', width: 140 },
  { title: i18n('schemaDiff.indexType'), dataIndex: 'indexType', key: 'indexType', width: 100 },
  { title: i18n('schemaDiff.unique'), dataIndex: 'unique', key: 'unique', width: 60, render: (v: any) => v ? 'YES' : 'NO' },
];

const fkColumns = [
  {
    title: i18n('schemaDiff.operation'),
    dataIndex: 'changeType',
    key: 'changeType',
    width: 72,
    render: (v: string) => <Tag color={changeTypeColor[v]}>{changeTypeLabel[v]}</Tag>,
  },
  { title: i18n('schemaDiff.foreignKeyName'), dataIndex: 'name', key: 'name', width: 140 },
  { title: i18n('schemaDiff.referencedTable'), dataIndex: 'refTable', key: 'refTable', width: 120 },
  { title: i18n('schemaDiff.referencedColumn'), dataIndex: 'refColumn', key: 'refColumn', width: 120 },
];

function buildColumnRows(diffs: IColumnDiff[]): any[] {
  return (diffs || []).map((d) => {
    const col = d.targetColumn || d.sourceColumn || {};
    return {
      key: `${d.changeType}-${col.name}-${Math.random()}`,
      changeType: d.changeType,
      name: col.name,
      columnType: col.dataType || col.columnType || '-',
      size: col.columnSize,
      nullable: col.nullable,
      defaultValue: col.defaultValue,
      comment: col.comment,
    };
  });
}

function buildIndexRows(diffs: IIndexDiff[]): any[] {
  return (diffs || []).map((d) => {
    const idx = d.targetIndex || d.sourceIndex || {};
    return {
      key: `${d.changeType}-${idx.name}`,
      changeType: d.changeType,
      name: idx.name,
      indexType: idx.type,
      unique: idx.unique,
    };
  });
}

function buildFkRows(diffs: IForeignKeyDiff[]): any[] {
  return (diffs || []).map((d) => {
    const fk = d.targetForeignKey || d.sourceForeignKey || {};
    return {
      key: `${d.changeType}-${fk.name}`,
      changeType: d.changeType,
      name: fk.name,
      refTable: fk.referencedTable,
      refColumn: fk.referencedColumn,
    };
  });
}

const DetailView: React.FC<DetailViewProps> = memo(({ tableDiff }) => {
  const hasColumns = tableDiff.columnDiffs && tableDiff.columnDiffs.length > 0;
  const hasIndexes = tableDiff.indexDiffs && tableDiff.indexDiffs.length > 0;
  const hasFKs = tableDiff.foreignKeyDiffs && tableDiff.foreignKeyDiffs.length > 0;
  const hasDdl = tableDiff.ddlStatement;

  if (!hasColumns && !hasIndexes && !hasFKs && !hasDdl) {
    return <div className={styles.empty}>{i18n('schemaDiff.noChanges')}</div>;
  }

  const tabItems = [];
  if (hasColumns) {
    tabItems.push({
      key: 'columns',
      label: `${i18n('schemaDiff.columns')} (${tableDiff.columnDiffs!.length})`,
      children: (
        <Table
          dataSource={buildColumnRows(tableDiff.columnDiffs!)}
          columns={columnColumns}
          size="small"
          pagination={false}
          bordered
        />
      ),
    });
  }
  if (hasIndexes) {
    tabItems.push({
      key: 'indexes',
      label: `${i18n('schemaDiff.indexes')} (${tableDiff.indexDiffs!.length})`,
      children: (
        <Table
          dataSource={buildIndexRows(tableDiff.indexDiffs!)}
          columns={indexColumns}
          size="small"
          pagination={false}
          bordered
        />
      ),
    });
  }
  if (hasFKs) {
    tabItems.push({
      key: 'foreignKeys',
      label: `${i18n('schemaDiff.foreignKeys')} (${tableDiff.foreignKeyDiffs!.length})`,
      children: (
        <Table
          dataSource={buildFkRows(tableDiff.foreignKeyDiffs!)}
          columns={fkColumns}
          size="small"
          pagination={false}
          bordered
        />
      ),
    });
  }
  if (hasDdl) {
    tabItems.push({
      key: 'ddl',
      label: i18n('schemaDiff.ddlPreview'),
      children: (
        <pre className={styles.ddlBlock}>{tableDiff.ddlStatement}</pre>
      ),
    });
  }

  return (
    <div className={styles.detailView}>
      <div className={styles.detailHeader}>
        <span className={styles.tableName}>{tableDiff.tableName}</span>
        <Tag color={tableDiff.diffType === 'MODIFIED' ? '#faad14' : tableDiff.diffType === 'ADDED' ? '#52c41a' : '#ff4d4f'}>
          {i18n(tableDiff.diffType === 'MODIFIED' ? 'schemaDiff.modified' : tableDiff.diffType === 'ADDED' ? 'schemaDiff.added' : 'schemaDiff.removed')}
        </Tag>
      </div>
      <Tabs items={tabItems} size="small" />
    </div>
  );
});

export default DetailView;
