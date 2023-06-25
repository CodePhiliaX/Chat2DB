import createRequest from "./base";
// import { IPageResponse,IPageParams,IHistoryRecord, IWindowTab, ISavedConsole } from '@/types';
import { ConsoleOpenedStatus, DatabaseTypeCode } from '@/constants/common'
import { ICreateConsole, IConsole, IPageResponse, IPageParams } from '@/typings/common';

export interface IGetSavedListParams extends IPageParams {
  dataSourceId?: string;
  databaseName?: string;
  ConsoleOpenedStatus?: ConsoleOpenedStatus;
}
export interface ISaveBasicInfo {
  name: string;
  type: DatabaseTypeCode;
  ddl: string;
  dataSourceId: number;
  databaseName: string;
}

export interface IUpdateWindowParams {
  id: number;
  name: string;
  ddl: string;
  dataSourceId: number;
  databaseName: string;
}

const saveConsole = createRequest<ICreateConsole, number>('/api/operation/saved/create', { method: 'post' });

const getWindowTab = createRequest<{ id: number }, number>('/api/operation/saved/:id', { method: 'get' });

const updateWindowTab = createRequest<IUpdateWindowParams, number>('/api/operation/saved/update', { method: 'post' });

const getSaveList = createRequest<IGetSavedListParams, IPageResponse<ISavedConsole>>('/api/operation/saved/list', {});

const deleteWindowTab = createRequest<{ id: number }, string>('/api/operation/saved/:id', { method: 'delete' });

const createHistory = createRequest<ISaveBasicInfo, void>('/api/operation/log/create', { method: 'post' });

const getHistoryList = createRequest<IGetSavedListParams, IPageResponse<IHistoryRecord>>('/api/operation/log/list', {});

export default {
  getSaveList,
  updateWindowTab,
  getHistoryList,
  saveConsole,
  deleteWindowTab,
  createHistory,
  getWindowTab
}