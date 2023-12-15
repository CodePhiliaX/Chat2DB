import { IConnectionListItem } from '@/typings/connection';
import { useWorkspaceStore } from './index'

export interface ICommonStore {
  currentConnectionDetails: IConnectionListItem | null;
}

export const initCommonStore: ICommonStore = {
  currentConnectionDetails: null,
}

export const setCurrentConnectionDetails = (connectionDetails: ICommonStore['currentConnectionDetails']) => {
  return useWorkspaceStore.setState({ currentConnectionDetails: connectionDetails });
}
