import type { StateCreator } from 'zustand/vanilla';
import { WorkspaceStore } from '../../store';
import { CommonState } from './initialState';

export interface CommonAction {
  setCurrentConnectionDetails: (data: CommonState['currentConnectionDetails']) => void;
  setCurrentWorkspaceExtend: (workspaceExtend: CommonState['currentWorkspaceExtend']) => void;
  setCurrentWorkspaceGlobalExtend: (workspaceGlobalExtend: CommonState['currentWorkspaceGlobalExtend']) => void;
}

export const createCommonAction: StateCreator<WorkspaceStore, [['zustand/devtools', never]], [], CommonAction> = (
  set,
) => ({
  setCurrentConnectionDetails: (data) => {
    set({ currentConnectionDetails: data });
  },
  setCurrentWorkspaceExtend: (workspaceExtend) => {
    set({ currentWorkspaceExtend: workspaceExtend });
  },
  setCurrentWorkspaceGlobalExtend: (workspaceGlobalExtend) => {
    set({ currentWorkspaceGlobalExtend: workspaceGlobalExtend });
  },
});
