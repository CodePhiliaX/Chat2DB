import { TreeNodeType } from '@/constants/tree';
import { DatabaseTypeCode } from '@/constants/database';

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
  treeNodeType: TreeNodeType;
  isLeaf?: boolean;
  children?: ITreeNode[];
  columnType?: string;
  extraParams?: IExtraParams; 
}