import { UseBoundStoreWithEqualityFn, createWithEqualityFn } from 'zustand/traditional';
import { devtools } from 'zustand/middleware';
import { shallow } from 'zustand/shallow';
import { StoreApi } from 'zustand';

import { IConnectionListItem, IConnectionEnv } from '@/typings/connection';
import connectionService from '@/service/connection';
export interface IConnectionStore {
  connectionList: IConnectionListItem[] | null;
  connectionEnvList: IConnectionEnv[] | null;
}

export const initConnectionStore = {
  connectionList: null,
  connectionEnvList: null,
};

export const useConnectionStore: UseBoundStoreWithEqualityFn<StoreApi<IConnectionStore>> = createWithEqualityFn(
  devtools(() => initConnectionStore),
  shallow
);

export const setConnectionList = (connectionList: IConnectionListItem[]) => {
  return useConnectionStore.setState({ connectionList });
};

export const setConnectionEnvList = (connectionEnvList: IConnectionEnv[]) => {
  return useConnectionStore.setState({ connectionEnvList });
};
export const getConnectionList: () => Promise<IConnectionListItem[]> = () => {
  return new Promise((resolve, reject) => {
    connectionService
      .getList({
        pageNo: 1,
        pageSize: 1000,
        refresh: true,
      })
      .then((res) => {
        const connectionList = res?.data || [];
        useConnectionStore.setState({ connectionList });
        resolve(connectionList);
      })
      .catch(() => {
        useConnectionStore.setState({ connectionList: [] });
        reject([]);
      });
  });
};
