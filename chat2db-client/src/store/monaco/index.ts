import { UseBoundStoreWithEqualityFn, createWithEqualityFn } from 'zustand/traditional';
import { devtools } from 'zustand/middleware';
import { shallow } from 'zustand/shallow';
import { StoreApi } from 'zustand';

// 表信息
export interface tableItem {
  columnList: {
    columnName?: string;
    columnType?: string;
  }[];
}

// schema信息
export interface schemaItem {
  tableList?: tableItem[];
}

// 数据库信息
export type databaseItem = {
  schemaList?: schemaItem[];
} | {
  databaseName: string;
  tableList: tableItem[];
}

// monaco store
export interface IMonacoStore {
  registerProvider: {
    // 数据源id
    [key: number]: databaseItem[]
  }
}

const initMonacoStore = {
  registerProvider: {}
}

export const useMonacoStore: UseBoundStoreWithEqualityFn<StoreApi<IMonacoStore>> = createWithEqualityFn(
  devtools(() => (initMonacoStore)),
  shallow
);

export const setRegisterProvider = (id: number, data: databaseItem[]) => {
  useMonacoStore.getState().registerProvider[id] = data;
}

