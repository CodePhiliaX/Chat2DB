import { CommonState, initCommonState } from './slices/common/initialState';
import { ConfigState, initConfigState } from './slices/config/initialState';
import { ConsoleState, initConsoleState } from './slices/console/initialState';
import { ModalState, initModalState } from './slices/modal/initialState';
import { TreeState, initTreeState } from './slices/tree/initialState';

export type WorkspaceState = CommonState & ConfigState & ConsoleState & ModalState & TreeState;

export const initialState: WorkspaceState = {
  ...initCommonState,
  ...initConfigState,
  ...initConsoleState,
  ...initModalState,
  ...initTreeState,
};
