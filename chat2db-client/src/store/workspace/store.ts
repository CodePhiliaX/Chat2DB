import { PersistOptions, devtools, persist } from 'zustand/middleware';
import { shallow } from 'zustand/shallow';
import { createWithEqualityFn } from 'zustand/traditional';
import { StateCreator } from 'zustand/vanilla';
import { WorkspaceState, initialState } from './initialState';
import { CommonAction, createCommonAction } from './slices/common/action';
import { ConfigAction, createConfigAction } from './slices/config/action';
import { ConsoleAction, createConsoleAction } from './slices/console/action'
import { ModalAction, createModalAction } from './slices/modal/action'

export type WorkspaceStore = WorkspaceState & CommonAction & ConfigAction & ConsoleAction & ModalAction;

const createStore: StateCreator<WorkspaceStore, [['zustand/devtools', never]]> = (...parameters) => ({
  ...initialState,
  ...createCommonAction(...parameters),
  ...createConfigAction(...parameters),
  ...createConsoleAction(...parameters),
  ...createModalAction(...parameters),
});

type GlobalPersist = Pick<WorkspaceStore, 'layout' | 'currentConnectionDetails'>;

// local-storage Options
const persistOptions: PersistOptions<WorkspaceStore, GlobalPersist> = {
  name: 'Chat2DB_Workspace',
  partialize: (state) => ({
    layout: state.layout,
    currentConnectionDetails: state.currentConnectionDetails,
  }),
};

export const useWorkspaceStore = createWithEqualityFn<WorkspaceStore>()(
  persist(
    devtools(createStore, {
      name: 'Chat2DB_Workspace',
    }),
    persistOptions,
  ),
  shallow,
);
