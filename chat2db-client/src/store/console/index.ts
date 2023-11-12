/**
 * 数据源的store
 */

import { create, UseBoundStore, StoreApi } from 'zustand';
import { devtools } from 'zustand/middleware';
import { IConnectionListItem, IConnectionEnv } from '@/typings/connection';
import connectionService from '@/service/connection';

export interface IConnectionStore {
  consoleList: IConnectionListItem[] | null;
  connectionEnvList: IConnectionEnv[] | null;
  setConnectionList: (connectionList: IConnectionListItem[]) => void;
  setConnectionEnvList: (connectionEnvList: IConnectionEnv[]) => void;
  getConnectionList: () => Promise<void>;
}

export const connectionStore = (set): IConnectionStore => ({
  consoleList: null,
  connectionEnvList: null,
  setConnectionList: (connectionList: IConnectionListItem[]) => set({ connectionList }),
  setConnectionEnvList: (connectionEnvList: IConnectionEnv[]) => set({ connectionEnvList }),
  getConnectionList: () => {
    return connectionService
      .getList({
        pageNo: 1,
        pageSize: 1000,
        refresh: true,
      })
      .then((res) => {
        set({ connectionList: res?.data || [] });
      })
      .catch(() => {
        set({ connectionList: [] });
      });
  },
});

export const useConnectionStore: UseBoundStore<StoreApi<IConnectionStore>> = create(
  devtools((set) => ({
    ...connectionStore(set),
  })),
);
