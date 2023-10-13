import { TreeNodeType, DatabaseTypeCode } from '@/constants';

export interface IExtraParams {
  databaseType?: DatabaseTypeCode;
  dataSourceName?: string;
  dataSourceId?: number;
  databaseName?: string;
  schemaName?: string;
  tableName?: string;
  functionName?: string;
  procedureName?: string;
  triggerName?: string;
}

export interface ITreeNode {
  key: string | number;
  name: string;
  treeNodeType: TreeNodeType; // 节点的类型 表、列、文件等等
  isLeaf?: boolean; // 是否为叶子节点
  children?: ITreeNode[];
  columnType?: string; // 列的类型
  extraParams?: IExtraParams;
  pinned?: boolean; // 是否置顶
  comment?: string; // 表列的注释
}

// 视图  函数  触发器  过程 通用的返回结果
export interface IRoutines {
  name: string; // 名称
  comment: string; // 描述
  pinned: boolean; // 是否置顶
}

export interface ITable {
   /**
    * 表描述
    */
   comment?: string | null;
   /**
    * 表名称
    */
   name: string | null;
   /**
    * 是否已经被固定
    */
   pinned?: boolean;

}
