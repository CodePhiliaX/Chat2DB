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
  success: boolean;
  uuid?: string;
  duration: number;
}
