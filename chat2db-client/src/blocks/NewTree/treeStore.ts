/**
 * 树的store
 */
import { create, UseBoundStore, StoreApi } from 'zustand';
import { devtools } from 'zustand/middleware';

export interface ITreeStore {
  focusId: number | string | null;
}

const treeStore = {
  focusId: null,
}

export const useTreeStore: UseBoundStore<StoreApi<ITreeStore>> = create(
  devtools(() => (treeStore)),
);

export const setFocusId = (focusId: ITreeStore['focusId']) => {
  useTreeStore.setState({ focusId });
}
