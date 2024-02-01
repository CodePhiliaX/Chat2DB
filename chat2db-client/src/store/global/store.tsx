import { PersistOptions, devtools, persist } from 'zustand/middleware';
import { shallow } from 'zustand/shallow';
import { createWithEqualityFn } from 'zustand/traditional';
import { StateCreator } from 'zustand/vanilla';
import { GlobalState, initialState } from './initialState';
import { CommonAction, createCommonAction } from './slices/common/action';

export type GlobalStore = GlobalState & CommonAction
// & SettingsAction;

const createStore: StateCreator<GlobalStore, [['zustand/devtools', never]]> = (...parameters) => ({
  ...initialState,
  ...createCommonAction(...parameters),
});

// local-storage Options
const persistOptions: PersistOptions<GlobalStore> = {
  name: 'Chat2DB_Global',
};

export const useGlobalStore = createWithEqualityFn<GlobalStore>()(
  persist(
    devtools(createStore, {
      name: 'Chat2DB_Global',
    }),
    persistOptions,
  ),
  shallow,
);
