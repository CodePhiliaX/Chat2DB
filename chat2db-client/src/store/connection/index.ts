/**
 * 数据源的store
 */

import { create, UseBoundStore, StoreApi } from 'zustand';
import { devtools } from 'zustand/middleware';
import { IConnectionListItem, IConnectionEnv } from '@/typings/connection';
import connectionService from '@/service/connection';
export interface IConnectionStore {
  connectionList: IConnectionListItem[] | null;
  connectionEnvList: IConnectionEnv[] | null;
  setConnectionList: (connectionList: IConnectionListItem[]) => void;
  setConnectionEnvList: (connectionEnvList: IConnectionEnv[]) => void;
  getConnectionList: () => Promise<IConnectionListItem[]>;
}

export const connectionStore = (set): IConnectionStore => ({
  connectionList: null,
  connectionEnvList: null,
  setConnectionList: (connectionList: IConnectionListItem[]) => set({ connectionList }),
  setConnectionEnvList: (connectionEnvList: IConnectionEnv[]) => set({ connectionEnvList }),
  getConnectionList: () => {
    return new Promise((resolve, reject) => {
       connectionService
        .getList({
          pageNo: 1,
          pageSize: 1000,
          refresh: true,
        })
        .then((res) => {
          const connectionList = res?.data || []
          set({ connectionList });
          resolve(connectionList);
        })
        .catch(() => {
          set({ connectionList: [] });
          reject([]);
        });
    });
  },
});

export const useConnectionStore: UseBoundStore<StoreApi<IConnectionStore>> = create(
  devtools((set) => ({
    ...connectionStore(set),
  })),
);
