/**
 * 数据源的store
 */

import { create, UseBoundStore, StoreApi } from 'zustand';
import { devtools } from 'zustand/middleware';
import { IConsole } from '@/typings/console';
import historyService from '@/service/history';

export interface IConsoleStore {
  consoleList: IConsole[] | null;
  activeConsoleId: string | null;
}

const initConsoleStore = {
  consoleList: null,
  activeConsoleId: null
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

export const setActiveConsoleId = (id: string) => {
  useConsoleStore.setState({ activeConsoleId: id });
}
