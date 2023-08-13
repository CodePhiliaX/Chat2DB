import { ConsoleOpenedStatus, ConsoleStatus, DatabaseTypeCode, OperationType } from '@/constants';

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
  operationType: OperationType;
}

export interface Option {
  value: number | string;
  label: string;
  isLeaf?: boolean;
  children?: Option[];
}

export type ICreateConsole = Omit<IConsole, 'id' | 'dataSourceName' | 'connectable'>;

export interface IUniversalTableParams {
  dataSourceId: string;
  databaseName?: string;
  schemaName?: string;
  tableName?: string;
}

/**
 * 版本返回
 * VersionResponse
 */
export interface IVersionResponse {
  /**
   * 基础链接
   * 类似于：http://test.sqlgpt.cn/gateway
   */
  baseUrl?: string;
  /**
   * 下载链接
   */
  downloadLink?: string;
  /**
   * 版本
   */
  version?: string;
  /**
   * 微信公众号名字
   */
  wechatMpName?: string;
}


