import type { StateCreator } from 'zustand/vanilla';
import { GlobalStore } from '../../store';
import { ICommandLineRequestListItem } from '@/service/commandLine';
import { ChildProcess } from 'child_process';

export interface RequestAction {
  // add ICommandLineRequestListItem
  addCommandLineRequestListItem: (data: ICommandLineRequestListItem) => void;
  // remove ICommandLineRequestListItem
  removeCommandLineRequestListItem: (id: string) => void;
  // update ICommandLineRequestListItem
  updateCommandLineRequestListItem: (id: string, data: ICommandLineRequestListItem) => void;
  // get
  setJavaServer: (child?: ChildProcess) => void;
}

export const createRequestAction: StateCreator<GlobalStore, [['zustand/devtools', never]], [], RequestAction> = (
  set,
) => ({
  addCommandLineRequestListItem: (data: ICommandLineRequestListItem) => {
    set((state) => {
      state.commandLineRequestList[data.requestData.id] = data;
      console.log('state.commandLineRequestList', state.commandLineRequestList)
      return state;
    });
  },
  removeCommandLineRequestListItem: (id: string) => {
    set((state) => {
      delete state.commandLineRequestList[id];
      return state;
    });
  },
  updateCommandLineRequestListItem: (id: string, data: ICommandLineRequestListItem) => {
    set((state) => {
      state.commandLineRequestList[id] = data;
      return state;
    });
  },
  setJavaServer: (child?: ChildProcess) => {
    set((state) => {
      state.javaServer = child;
      return state;
    });
  },
});
