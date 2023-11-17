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
  refresh?: boolean;
}

export interface IPagingData {
  hasNextPage?: boolean;
  pageNo: number;
  pageSize: number;
  total: number;
}

export interface Option {
  value: number | string;
  label: string;
  isLeaf?: boolean;
  children?: Option[];
}


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


