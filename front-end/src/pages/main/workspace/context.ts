import {getCurrentWorkspaceDatabase, setCurrentWorkspaceDatabase } from '@/utils/localStorage';

export type ICurrentDatabase = {
  databaseSourceName?: string;
  dataSourceId?: number;
  databaseName?: string;
  schemaName?: string;
}

export interface IState {
  currentDatabase: ICurrentDatabase;
}

export enum workspaceActionType {
  CURRENT_DATABASE = 'currentDatabase',
}

export interface IAction {
  type: workspaceActionType;
  payload?: any;
}

export const initState = {
  currentDatabase: getCurrentWorkspaceDatabase()
}

export const reducer = (preState: IState, action: IAction ) => {
  const { type, payload } = action;

  switch(type) {
    case workspaceActionType.CURRENT_DATABASE:
      return changeCurrentDatabase(preState,payload);
  }
}

function changeCurrentDatabase(preState:IState, payload:any){
  setCurrentWorkspaceDatabase(payload);
  return {
    ...preState,
    currentDatabase: payload,
  }
}