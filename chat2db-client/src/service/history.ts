import createRequest from "./base";
// import { IPageResponse,IPageParams,IHistoryRecord, IWindowTab, ISavedConsole } from '@/types';
import { ConsoleOpenedStatus, DatabaseTypeCode, ConsoleStatus } from '@/constants'
import { ICreateConsole, IConsole, IPageResponse, IPageParams } from '@/typings';

export interface IGetSavedListParams extends IPageParams {
  dataSourceId?: number;
  databaseName?: string;
  tabOpened?: ConsoleOpenedStatus;
  status?: ConsoleStatus
}
export interface IGetHistoryListParams extends IPageParams { 
  dataSourceId?: number;
  databaseName?: string;
}
export interface ISaveBasicInfo {
  name: string;
  type: DatabaseTypeCode;
  ddl: string;
  dataSourceId: number;
  databaseName: string;
}

export interface IUpdateConsoleParams {
  id: number;
}

export interface IHistoryRecord { 
 /**
 * 是否可连接
 */
 connectable?: boolean | null;
 /**
  * DB名称
  */
 databaseName?: null | string;
 /**
  * 数据源id
  */
 dataSourceId?: number | null;
 /**
  * 数据源名称
  */
 dataSourceName?: null | string;
 /**
  * ddl内容
  */
 ddl?: null | string;
 /**
  * 扩展信息
  */
 extendInfo?: null | string;
 /**
  * 主键
  */
 id?: number | null;
 /**
  * 文件别名
  */
 name?: null | string;
 /**
  * 操作行数
  */
 operationRows?: number | null;
 /**
  * schema名称
  */
 schemaName?: null | string;
 /**
  * 状态
  */
 status?: null | string;
 /**
  * ddl语言类型
  */
 type?: null | string;
 /**
  * 使用时长
  */
 useTime?: number | null;
  /**
  * 创建时间
  */
 gmtCreate: string;
}

const saveConsole = createRequest<ICreateConsole, number>('/api/operation/saved/create', { method: 'post' });

// orderByDesc true 降序
const getWindowTab = createRequest<{ id: number, orderByDesc: boolean }, number>('/api/operation/saved/:id', { method: 'get' });

const updateSavedConsole = createRequest<Partial<IConsole> & {id: number}, number>('/api/operation/saved/update', { method: 'post' });

const getSavedConsoleList = createRequest<IGetSavedListParams, IPageResponse<IConsole>>('/api/operation/saved/list', {});

const deleteSavedConsole = createRequest<{ id: number }, string>('/api/operation/saved/:id', { method: 'delete' });

const createHistory = createRequest<ISaveBasicInfo, void>('/api/operation/log/create', { method: 'post' });

const getHistoryList = createRequest<IGetHistoryListParams, IPageResponse<IHistoryRecord>>('/api/operation/log/list', {});

export default {
  getSavedConsoleList,
  updateSavedConsole,
  getHistoryList,
  saveConsole,
  deleteSavedConsole,
  createHistory,
  getWindowTab
}
