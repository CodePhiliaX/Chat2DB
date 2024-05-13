import { UseBoundStoreWithEqualityFn, createWithEqualityFn } from 'zustand/traditional';
import { devtools, persist } from 'zustand/middleware';
import { shallow } from 'zustand/shallow';
import { StoreApi } from 'zustand';

export interface IMainStore {
  mainPageActiveTab: string;
}

const initMainStore = {
  mainPageActiveTab: 'connections',
};

export const useMainStore: UseBoundStoreWithEqualityFn<StoreApi<IMainStore>> = createWithEqualityFn(
  devtools(
    persist(() => initMainStore, {
      name: 'main-page-store',
      getStorage: () => localStorage,
      // 工作区的状态只保存 layout布局信息
      partialize: (state: IMainStore) => ({
        mainPageActiveTab: state.mainPageActiveTab,
      }),
    }),
  ),
  shallow,
);

export const setMainPageActiveTab = (mainPageActiveTab: string) => {
  return useMainStore.setState({
    mainPageActiveTab,
  });
};
