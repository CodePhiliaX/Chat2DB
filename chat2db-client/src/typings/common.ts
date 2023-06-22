import { DatabaseTypeCode } from '@/constants/database';
export interface IPageResponse<T> {
  data: T[];
  pageNo: number;
  pageSize: number;
  total: number;
  hasNextPage?: boolean;
}

export interface ISaveConsole {
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
}

export interface IConsole {
  id: number;
  name: string;
  ddl: string;
  dataSourceId: number;
  databaseName: string;
  dataSourceName: string;
  schemaName: string;
  databaseType: DatabaseTypeCode;
  status: string;
}