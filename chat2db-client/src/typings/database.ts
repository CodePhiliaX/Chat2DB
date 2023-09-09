import { DatabaseTypeCode, TableDataType } from '@/constants';

export interface IDatabase {
  name: string;
  code: DatabaseTypeCode;
  img: string;
  icon: string;
}

export interface ITableHeaderItem {
  dataType: TableDataType;
  name: string;
}

export interface IManageResultData {
  dataList: string[][];
  headerList: ITableHeaderItem[];
  description: string;
  message: string;
  sql: string;
  originalSql: string;
  success: boolean;
  uuid?: string;
  duration: number;
  fuzzyTotal: string;
  hasNextPage: boolean;
  sqlType: 'SELECT' | 'UNKNOWN';
}

/** 查询结果 配置属性 */
export interface IResultConfig {
  pageNo: number;
  pageSize: number;
  total: number | string;
  hasNextPage: boolean;
}

/** 不同数据库支持的列字段类型*/
export interface IDatabaseFieldType {
  typeName: string;
}

export interface IColumn {
  name: string;
  dataType: string;
  columnType: string;
  nullable: 0 | 1;
  primaryKey: boolean;
  defaultValue: string;
  autoIncrement: boolean;
  numericPrecision: number;
  numericScale: number;
  characterMaximumLength: number;
  comment: string;
}
export interface IIndex {
  columns: string;
  name: string;
  type: string;
  comment: string;
  columnList: IColumn[];
}

/** 数据库表的详情*/
export interface IDatabaseTableDetail {
  name: string;
  comment: string;
  pinned: false;
  ddl: string;
  columnList: IColumn[];
  indexList: IIndex[];
}
