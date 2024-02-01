import { produce } from 'immer';
import type { StateCreator } from 'zustand/vanilla';
import { WorkspaceStore } from '../../store';
import { ConfigState } from './initialState';

export interface ConfigAction {
  togglePanelRight: () => void;
  togglePanelLeft: () => void;
  setPanelLeftWidth: (width: number) => void;
  setPanelRightWidth: (width: number) => void;
}

export const createConfigAction : StateCreator<WorkspaceStore, [['zustand/devtools', never]], [], ConfigAction> = (
  set,
) => ({
  togglePanelRight: () => {
    set(
      produce((state: ConfigState) => {
        state.layout.panelRight = !state.layout.panelRight;
      })
    );
  },
  togglePanelLeft: () => {
    set(
      produce((state: ConfigState) => {
        state.layout.panelLeft = !state.layout.panelLeft;
      })
    );
  },
  setPanelLeftWidth: (width: number) => {
    set(
      produce((state: ConfigState) => {
        state.layout.panelLeftWidth = width;
      })
    );
  },
  setPanelRightWidth: (width: number) => {
    set(
      produce((state: ConfigState) => {
        state.layout.panelRightWidth = width;
      })
    );
  },
});
