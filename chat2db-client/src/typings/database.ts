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
