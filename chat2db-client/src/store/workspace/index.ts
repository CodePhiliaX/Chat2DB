import { create, UseBoundStore, StoreApi } from 'zustand';
import { devtools, persist } from 'zustand/middleware';

import { configStore, IConfigStore } from './config';
import { consoleStore, IConsoleStore } from './console';

export type IStore = IConfigStore & IConsoleStore;

export const useWorkspaceStore: UseBoundStore<StoreApi<IStore>> = create(
  devtools(
    persist(
      (set) => ({
        ...configStore(set),
        ...consoleStore(set),
      }),
      // persist config
      {
        name: 'workspace-store',
        getStorage: () => localStorage,
        // 工作区的状态只保存 layout
        partialize: (state: IStore) => ({ layout: state.layout }),
      },
    ),
  ),
);
