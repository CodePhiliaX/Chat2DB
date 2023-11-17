import { UseBoundStoreWithEqualityFn, createWithEqualityFn } from 'zustand/traditional';
import { devtools } from 'zustand/middleware';
import { shallow } from 'zustand/shallow';
import { StoreApi } from 'zustand';
import { IConsole, ICreateConsoleParams } from '@/typings';
import { IWorkspaceTab } from '@/typings/workspace';
import historyService from '@/service/history';
import { ConsoleStatus, WorkspaceTabType } from '@/constants'

export interface IConsoleStore {
  consoleList: IConsole[] | null;
  activeConsoleId: string | number | null;
  workspaceTabList: IWorkspaceTab[] | null;
}

const initConsoleStore = {
  consoleList: null,
  activeConsoleId: null,
  workspaceTabList: null,
}

export const useConsoleStore: UseBoundStoreWithEqualityFn<StoreApi<IConsoleStore>> = createWithEqualityFn(
  devtools(() => (initConsoleStore)),
  shallow
);

export const getSavedConsoleList = () => {
  historyService.getSavedConsoleList({
    tabOpened: 'y',
    pageNo: 1,
    pageSize: 20,
  }).then((res) => {
    useConsoleStore.setState({ consoleList: res?.data });
  });
}

export const setActiveConsoleId = (id: IConsoleStore['activeConsoleId']) => {
  useConsoleStore.setState({ activeConsoleId: id });
}

export const setWorkspaceTabList = (items: IConsoleStore['workspaceTabList']) => {
  useConsoleStore.setState({ workspaceTabList: items });
}

export const createConsole = (params: ICreateConsoleParams)=>{
  const workspaceTabList = useConsoleStore.getState().workspaceTabList;
  const newConsole = {
    ...params,
    name: params.name || 'create console',
    ddl: params.ddl || '',
    status: ConsoleStatus.DRAFT,
    operationType: WorkspaceTabType.CONSOLE,
  };

  return new Promise((resolve) => {
    historyService.createConsole(newConsole).then((res) => {
      const newList = [
        ...(workspaceTabList||[]),
        {
          id: res,
          title: newConsole.name,
          type: newConsole.operationType,
          uniqueData: newConsole,
        },
      ];
      setWorkspaceTabList(newList);
      setActiveConsoleId(res);
      resolve(res);
    });
  });
}

export const addWorkspaceTab = (params: IWorkspaceTab) => {
  const workspaceTabList = useConsoleStore.getState().workspaceTabList;
  if(workspaceTabList?.findIndex((item) => item?.id === params?.id) !== -1){
    setActiveConsoleId(params.id);
    return;
  }
  
  const newList = [
    ...(workspaceTabList||[]),
    params,
  ];

  setWorkspaceTabList(newList);
  setActiveConsoleId(params.id);
};
