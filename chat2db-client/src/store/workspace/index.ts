import { create, UseBoundStore, StoreApi } from 'zustand';
import { devtools, persist } from 'zustand/middleware';

import { configStore, IConfigStore } from './config';
import { consoleStore, IConsoleStore } from './console';
import { commonStore, ICommonStore } from './common';
import { modalStore , IModalStore } from './modal';

export type IStore = IConfigStore & IConsoleStore & ICommonStore & IModalStore;

export const useWorkspaceStore: UseBoundStore<StoreApi<IStore>> = create(
  devtools(
    persist(
      (set) => ({
        ...configStore(set),
        ...consoleStore(set),
        ...commonStore(set),
        ...modalStore(),
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
      name: "workspaceStore"
    }
  ),
);
