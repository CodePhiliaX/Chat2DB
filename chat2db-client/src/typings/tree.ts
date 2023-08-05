import { TreeNodeType, DatabaseTypeCode } from '@/constants';

export interface IExtraParams {
  databaseType?: DatabaseTypeCode;
  dataSourceName?: string;
  dataSourceId?: number;
  databaseName?: string;
  schemaName?: string;
  tableName?: string;
};

export interface ITreeNode {
  key: string | number;
  name: string;
  treeNodeType: TreeNodeType; // 节点的类型 表、列、文件等等
  isLeaf?: boolean; // 是否为叶子节点
  children?: ITreeNode[];
  columnType?: string; // 列的类型
  extraParams?: IExtraParams;
  pinned?: boolean; // 是否置顶
  comment: string; // 表列的注释
}