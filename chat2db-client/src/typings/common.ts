import { DatabaseTypeCode } from '@/constants/database';
import { ConsoleOpenedStatus, ConsoleStatus } from '@/constants/common';
export interface IPageResponse<T> {
  data: T[];
  pageNo: number;
  pageSize: number;
  total: number;
  hasNextPage?: boolean;
}


export interface IPageParams {
  searchKey?: string;
  pageNo: number;
  pageSize: number;
}

export interface IConsole {
  id: number;
  name: string;
  ddl: string;
  dataSourceId: number;
  databaseName: string;
  dataSourceName: string;
  schemaName: string;
  type: DatabaseTypeCode;
  status: string;
  connectable: boolean;
  tabOpened?: ConsoleOpenedStatus;
}

export type ICreateConsole = Omit<IConsole, 'id' | 'dataSourceName' | 'schemaName'>

