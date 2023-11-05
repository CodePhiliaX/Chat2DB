import { create, UseBoundStore, StoreApi } from 'zustand';
import { devtools, persist } from 'zustand/middleware';

import { configStore, IConfigStore } from './config';
import { copyFocusedContent, ICopyFocusedContent } from './copyFocusedContent';

export type IStore = IConfigStore & ICopyFocusedContent;

export const useWorkspaceStore: UseBoundStore<StoreApi<IStore>> = create(
  devtools(
    persist(
      (set) => ({
        ...configStore(set),
        ...copyFocusedContent(set),
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
