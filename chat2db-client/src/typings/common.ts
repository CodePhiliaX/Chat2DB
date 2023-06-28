import { ConsoleOpenedStatus, ConsoleStatus, DatabaseTypeCode } from '@/constants';
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
  dataSourceName: string;
  databaseName?: string;
  schemaName?: string;
  type: DatabaseTypeCode;
  status: ConsoleStatus;
  connectable: boolean;
  tabOpened?: ConsoleOpenedStatus;
}

export type ICreateConsole = Omit<IConsole, 'id' | 'dataSourceName' | 'connectable'>

