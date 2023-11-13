/**
 * 数据源的store
 */

import { create, UseBoundStore, StoreApi } from 'zustand';
import { devtools } from 'zustand/middleware';
import { IConsole } from '@/typings/console';
import { IWorkspaceTab } from '@/typings/workspace';
import historyService from '@/service/history';

export interface IConsoleStore {
  consoleList: IConsole[] | null;
  activeConsoleId: string | number | null;
  workspaceTabList: IWorkspaceTab[] | null;
}

const initConsoleStore = {
  consoleList: null,
  activeConsoleId: null,
  workspaceTabList: null,
}

export const useConsoleStore: UseBoundStore<StoreApi<IConsoleStore>> = create(
  devtools(() => (initConsoleStore)),
);

export const getSavedConsoleList = () => {
  historyService.getSavedConsoleList({
    tabOpened: 'y',
    pageNo: 1,
    pageSize: 20,
  }).then((res) => {
    useConsoleStore.setState({ consoleList: res?.data });
  });
}

export const setActiveConsoleId = (id: IConsoleStore['activeConsoleId']) => {
  useConsoleStore.setState({ activeConsoleId: id });
}

export const setWorkspaceTabList = (items: IConsoleStore['workspaceTabList']) => {
  useConsoleStore.setState({ workspaceTabList: items });
}
