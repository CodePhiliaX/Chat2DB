import { IWorkspaceTab } from '@/typings/workspace';
import { IConsole } from '@/typings';

export interface ConsoleState {
  consoleList: IConsole[] | null;
  savedConsoleList: IConsole[] | null;
  activeConsoleId: string | number | null;
  workspaceTabList: IWorkspaceTab[] | null;
  createConsoleLoading: boolean
}

export const initConsoleState = {
  consoleList: null,
  savedConsoleList: null,
  activeConsoleId: null,
  workspaceTabList: null,
  createConsoleLoading: false,
};
