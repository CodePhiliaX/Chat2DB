import type { StateCreator } from 'zustand/vanilla';
import { WorkspaceStore } from '../../store';

export interface ModalAction {
  setOpenCreateDatabaseModal: (fn: any) => void;
}

export const createModalAction: StateCreator<WorkspaceStore, [['zustand/devtools', never]], [], ModalAction> = (
  set,
) => ({
  setOpenCreateDatabaseModal: (fn) => {
    set({
      openCreateDatabaseModal: fn,
    });
  },
});
