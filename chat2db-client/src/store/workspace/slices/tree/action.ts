import type { StateCreator } from 'zustand/vanilla';
import { WorkspaceStore } from '../../store';
import { TreeState, initTreeState } from './initialState';

export interface TreeAction {
  setFocusId: (focusId: TreeState['focusId']) => void;
  setFocusTreeNode: (focusTreeNode: TreeState['focusTreeNode']) => void;
  clearTreeStore: () => void;
}

export const createTreeAction: StateCreator<WorkspaceStore, [['zustand/devtools', never]], [], TreeAction> = (
  set,
) => ({
  setFocusId: (focusId) => {
    set({ focusId });
  },
  setFocusTreeNode: (focusTreeNode) => {
    set({ focusTreeNode });
  },
  clearTreeStore: () => {
    set(initTreeState);
  },

});
