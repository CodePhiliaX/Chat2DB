import { UseBoundStoreWithEqualityFn, createWithEqualityFn } from 'zustand/traditional';
import { devtools } from 'zustand/middleware';
import { shallow } from 'zustand/shallow';
import { StoreApi } from 'zustand';

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

export const useMonacoStore: UseBoundStoreWithEqualityFn<StoreApi<IMonacoStore>> = createWithEqualityFn(
  devtools(() => (initMonacoStore)),
  shallow
);

