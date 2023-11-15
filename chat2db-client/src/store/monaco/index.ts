/**
 * 数据源的store
 */

import { create, UseBoundStore, StoreApi } from 'zustand';
import { devtools } from 'zustand/middleware';

export interface IMonacoStore {
  registerProvider: {
    [key: string]: {
      databaseNameList: (databaseName: Array<{ 
        name: string;
        dataSourceName: string,
      }>) => void;
    }
  } | null
}

const initMonacoStore = {
  registerProvider: null
}

export const useMonacoStore: UseBoundStore<StoreApi<IMonacoStore>> = create(
  devtools(() => (initMonacoStore)),
);

