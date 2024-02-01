import type { StateCreator } from 'zustand/vanilla';
import { WorkspaceStore } from '../../store';
import { ConsoleState } from './initialState';
import { ICreateConsoleParams } from '@/typings';
import historyService from '@/service/history';
import { ConsoleStatus, WorkspaceTabType } from '@/constants';
import { message } from 'antd';
import i18n from '@/i18n';

export interface ConsoleAction {
  getOpenConsoleList: () => void;
  getSavedConsoleList: () => void;
  setActiveConsoleId: (data: ConsoleState['activeConsoleId']) => void;
  setWorkspaceTabList: (data: ConsoleState['workspaceTabList']) => void;
  createConsole: (params: ICreateConsoleParams) => Promise<any>;
  addWorkspaceTab: (params: any) => void;
}

export const createConsoleAction: StateCreator<WorkspaceStore, [['zustand/devtools', never]], [], ConsoleAction> = (
  set,
  get,
) => ({
  getOpenConsoleList: () => {
    historyService
      .getConsoleList({
        tabOpened: 'y',
        pageNo: 1,
        pageSize: 20,
      })
      .then((res) => {
        set({
          consoleList: res?.data,
        });
      });
  },
  getSavedConsoleList: () => {
    historyService
      .getConsoleList({
        pageNo: 1,
        pageSize: 100,
        status: ConsoleStatus.RELEASE,
      })
      .then((res) => {
        set({
          savedConsoleList: res?.data,
        });
      });
  },
  setActiveConsoleId: (data) => {
    set({
      activeConsoleId: data,
    });
  },
  setWorkspaceTabList: (data) => {
    set({
      workspaceTabList: data,
    });
  },
  createConsole: (params) => {
    const workspaceTabList = get().workspaceTabList;
    const currentConnectionDetails = get().currentConnectionDetails;
    const newConsole = {
      ...params,
      name: params.name || `untitled-${params.databaseName || params.schemaName} (${params.dataSourceName})`,
      ddl: params.ddl || '',
      status: ConsoleStatus.DRAFT,
      operationType: params.operationType || WorkspaceTabType.CONSOLE,
      type: params.databaseType,
      supportDatabase: currentConnectionDetails?.supportDatabase,
      supportSchema: currentConnectionDetails?.supportSchema,
    };

    return new Promise((resolve) => {
      if ((workspaceTabList?.length || 0) >= 20) {
        message.warning(i18n('workspace.tips.maxConsole'));
        return;
      }
      set({ createConsoleLoading: true });
      historyService
        .createConsole(newConsole)
        .then((res) => {
          const newList = [
            ...(workspaceTabList || []),
            {
              id: res,
              title: newConsole.name,
              type: newConsole.operationType,
              uniqueData: newConsole,
            },
          ];

          get().setWorkspaceTabList(newList);
          get().setActiveConsoleId(res);
          resolve(res);
        })
        .finally(() => {
          set({ createConsoleLoading: false });
        });
    });
  },
  addWorkspaceTab: (params) => {
    const workspaceTabList = get().workspaceTabList;
    if (workspaceTabList?.findIndex((item) => item?.id === params?.id) !== -1) {
      get().setActiveConsoleId(params.id);
      return;
    }

    const newList = [...(workspaceTabList || []), params];

    get().setWorkspaceTabList(newList);
    get().setActiveConsoleId(params.id);
  },
});
