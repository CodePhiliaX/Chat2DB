import createRequest from "./base";
import { IPageResponse, IConnectionBase,IPageParams,IHistoryRecord, IWindowTab, ISavedConsole } from '@/types';
import { DatabaseTypeCode, ConsoleStatus, TabOpened } from '@/utils/constants'

export interface IGetHistoryListParams extends IPageParams  {
  dataSourceId?: string;
  databaseName?: string;
  tabOpened?: TabOpened;
}
export interface ISaveBasicInfo {
  name: string;
  type: DatabaseTypeCode;
  ddl: string;
  dataSourceId: number;
  databaseName: string;
}
export interface ISaveConsole extends ISaveBasicInfo {
  status: ConsoleStatus;
  tabOpened: TabOpened;
}

export interface IUpdateWindowParams {
  id: number;
  name: string;
  ddl: string;
  dataSourceId: number;
  databaseName: string;
}

const saveWindowTab = createRequest<ISaveConsole, number>('/api/operation/saved/create', { method: 'post' });

const getWindowTab = createRequest<{id:string}, number>('/api/operation/saved/:id',{method: 'get'});

const updateWindowTab = createRequest<IUpdateWindowParams, number>('/api/operation/saved/update',{method: 'post'});

const getSaveList = createRequest<IGetHistoryListParams, IPageResponse<ISavedConsole>>('/api/operation/saved/list',{});

const deleteWindowTab = createRequest<{id:string}, string>('/api/operation/saved/:id',{method: 'delete'});

const createHistory = createRequest<ISaveBasicInfo, void>('/api/operation/log/create',{method: 'post'});

const getHistoryList = createRequest<IGetHistoryListParams, IPageResponse<IHistoryRecord>>('/api/operation/log/list',{});

export default {
  getSaveList,
  updateWindowTab,
  getHistoryList,
  saveWindowTab,
  deleteWindowTab,
  createHistory,
  getWindowTab
}