import createRequest from "./base";
// import { IPageResponse,IPageParams,IHistoryRecord, IWindowTab, ISavedConsole } from '@/types';
import { ConsoleOpenedStatus, DatabaseTypeCode, ConsoleStatus } from '@/constants'
import { ICreateConsole, IConsole, IPageResponse, IPageParams } from '@/typings';

export interface IGetSavedListParams extends IPageParams {
  dataSourceId?: string;
  databaseName?: string;
  tabOpened?: ConsoleOpenedStatus;
  status?: ConsoleStatus
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

const saveConsole = createRequest<ICreateConsole, number>('/api/operation/saved/create', { method: 'post' });

// orderByDesc true 降序
const getWindowTab = createRequest<{ id: number, orderByDesc: boolean }, number>('/api/operation/saved/:id', { method: 'get' });

const updateSavedConsole = createRequest<Partial<IConsole> & {id: number}, number>('/api/operation/saved/update', { method: 'post' });

const getSavedConsoleList = createRequest<IGetSavedListParams, IPageResponse<IConsole>>('/api/operation/saved/list', {});

const deleteSavedConsole = createRequest<{ id: number }, string>('/api/operation/saved/:id', { method: 'delete' });

const createHistory = createRequest<ISaveBasicInfo, void>('/api/operation/log/create', { method: 'post' });

const getHistoryList = createRequest<IGetSavedListParams, IPageResponse<IHistoryRecord>>('/api/operation/log/list', {});

export default {
  getSavedConsoleList,
  updateSavedConsole,
  getHistoryList,
  saveConsole,
  deleteSavedConsole,
  createHistory,
  getWindowTab
}