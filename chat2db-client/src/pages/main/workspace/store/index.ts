import { UseBoundStoreWithEqualityFn, createWithEqualityFn } from 'zustand/traditional';
import { devtools, persist } from 'zustand/middleware';
import { shallow } from 'zustand/shallow';
import { StoreApi } from 'zustand';

import { initConfigStore, IConfigStore } from './config';
import { initConsoleStore, IConsoleStore } from './console';
import { initCommonStore, ICommonStore } from './common';
import { initModalStore, IModalStore } from './modal';

export type IStore = IConfigStore & IConsoleStore & ICommonStore & IModalStore;

export const useWorkspaceStore: UseBoundStoreWithEqualityFn<StoreApi<IStore>> = createWithEqualityFn(
  devtools(
    persist(
      () => ({
        ...initConsoleStore,
        ...initConfigStore,
        ...initCommonStore,
        ...initModalStore,
      }),
      // persist config
      {
        name: 'workspace-store',
        getStorage: () => localStorage,
        // 工作区的状态只保存 layout布局信息
        partialize: (state: IStore) => ({
          layout: state.layout,
          currentConnectionDetails: state.currentConnectionDetails,
        }),
      },
    ),
    {
      name: 'workspaceStore',
    },
  ),
  shallow,
);
