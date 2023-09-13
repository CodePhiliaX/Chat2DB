import { IndexesType } from '@/constants';

// 编辑表时表的基础数据
export interface IBaseInfo {
  name: string;
  comment?: string;
}

// 编辑表时列的数据结构
export interface IColumnItem {
  key: string;
  name: string;
  columnType: string | null;
  columnSize: number;
  // length: number | null;
  nullable: number;
  prefixLength?: number | null;
  comment?: string;
  primaryKey?: boolean;
  defaultValue?: string;
  // dataType: string;
  // autoIncrement: boolean;
  // numericPrecision: number;
  // numericScale: number;
  // characterMaximumLength: number;
}


// 编辑表时索引的数据结构
export interface IIndexItem {
  key: string;
  name: string;
  comment?: string;
  type: IndexesType | null;
  columnList: IColumnItem[];
}

// 编辑表时整体的数据结构
export interface IEditTableInfo extends IBaseInfo {  
  columnList: IColumnItem[];
  indexList: IIndexItem[];
}