import { getCurrentWorkspaceDatabase, setCurrentWorkspaceDatabase } from '@/utils/localStorage';
import { ITreeNode } from '@/typings';
import { TreeNodeType, DatabaseTypeCode } from '@/constants';

export type ICurrentWorkspaceData = {
  dataSourceId: number;
  databaseSourceName: string;
  databaseName: string;
  databaseType: DatabaseTypeCode;
  schemaName?: string;
}

export interface IState {
  currentWorkspaceData: ICurrentWorkspaceData;
  dblclickTreeNodeData: ITreeNode | undefined;
}

export enum workspaceActionType {
  CURRENT_WORKSPACE_DATA = 'currentWorkspaceData',
  DBLCLICK_TREE_NODE = 'dblclickTreeNodeData',
}

export interface IAction {
  type: workspaceActionType;
  payload?: any;
}

export const initState: IState = {
  currentWorkspaceData: getCurrentWorkspaceDatabase(),
  dblclickTreeNodeData: undefined
}

export const reducer = (preState: IState, action: IAction) => {
  const { type, payload } = action;

  switch (type) {
    case workspaceActionType.CURRENT_WORKSPACE_DATA:
      return changeCurrentWorkspaceData(preState, payload);
    case workspaceActionType.DBLCLICK_TREE_NODE:
      return {
        ...preState,
        dblclickTreeNodeData: payload
      }
  }
}

function changeCurrentWorkspaceData(preState: IState, payload: any) {
  setCurrentWorkspaceDatabase(payload);
  return {
    ...preState,
    currentWorkspaceData: payload,
  }
}