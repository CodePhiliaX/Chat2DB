import { TreeNodeType, DatabaseTypeCode } from '@/constants';

export interface IExtraParams {
  dataSourceId: number;
  databaseType: DatabaseTypeCode;
  dataSourceName: string;
  databaseName?: string;
  schemaName?: string;
  tableName?: string;
  functionName?: string;
  procedureName?: string;
  triggerName?: string;
}

export interface ITreeNode {
  uuid: string;
  key: string | number;
  name: string;
  // 用展示的name
  // displayName: string;
  treeNodeType: TreeNodeType; // 节点的类型 表、列、文件等等
  pretendNodeType?: TreeNodeType; // 伪装的节点类型，当树不连续时，需要用到
  isLeaf?: boolean; // 是否为叶子节点
  children?: ITreeNode[] | null;
  columnType?: string; // 列的类型
  extraParams?: IExtraParams;
  pinned?: boolean; // 是否置顶
  comment?: string; // 表列的注释
  loadData?: (params:{refresh: boolean}) => void; // 加载数据的方法
  // 父元素
  parentNode?: ITreeNode;
  level?: number; // 层级
  // 是否展开
  expanded?: boolean;
  parentId?: string;
  // 分页
  page?: number;
  pageSize?: number;
  total?: number;
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
