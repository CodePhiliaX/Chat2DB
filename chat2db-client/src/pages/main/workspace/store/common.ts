import { IConnectionListItem } from '@/typings/connection';
import { useWorkspaceStore } from './index'

export interface ICommonStore {
  currentConnectionDetails: IConnectionListItem | null;
  currentWorkspaceExtend: string | null;
}

export const initCommonStore: ICommonStore = {
  currentConnectionDetails: null,
  currentWorkspaceExtend: null,
}

export const setCurrentConnectionDetails = (connectionDetails: ICommonStore['currentConnectionDetails']) => {
  return useWorkspaceStore.setState({ currentConnectionDetails: connectionDetails });
}

export const setCurrentWorkspaceExtend = (workspaceExtend: ICommonStore['currentWorkspaceExtend']) => {
  return useWorkspaceStore.setState({ currentWorkspaceExtend: workspaceExtend });
}
