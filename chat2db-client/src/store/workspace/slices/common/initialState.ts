import { IConnectionListItem } from '@/typings/connection';

export interface CommonState {
  currentConnectionDetails: IConnectionListItem | null;
  currentWorkspaceExtend: string | null;
  currentWorkspaceGlobalExtend: {
    code: string,
    uniqueData: any,
  } | null;
}

export const initCommonState: CommonState = {
  currentConnectionDetails: null,
  currentWorkspaceExtend: null,
  currentWorkspaceGlobalExtend: null,
}
