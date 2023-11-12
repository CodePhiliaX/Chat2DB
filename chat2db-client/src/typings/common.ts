import { ConsoleOpenedStatus, ConsoleStatus, DatabaseTypeCode, WorkspaceTabType } from '@/constants';

export type NonNullable<T> = T extends null | undefined ? never : T;

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

export interface IPagingData {
  hasNextPage?: boolean;
  pageNo: number;
  pageSize: number;
  total: number;
}

// 控制台详情
export interface IConsole {
  id: number; // consoleId
  name: string; // 控制台名称
  ddl: string; // 控制台内的sql
  dataSourceId: number; // 数据源id
  dataSourceName: string; // 数据源名称
  databaseName?: string; // 数据库名称
  schemaName?: string; // schema名称
  type: DatabaseTypeCode; // 数据库类型
  status: ConsoleStatus; // 控制台状态
  connectable: boolean; // 是否可连接
  tabOpened?: ConsoleOpenedStatus; // 控制台tab是否打开
  operationType: WorkspaceTabType; // 操作类型
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


