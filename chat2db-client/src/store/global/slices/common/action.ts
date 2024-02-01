import type { StateCreator } from 'zustand/vanilla';
import { GlobalStore } from '../../store';
import { CommonState } from './initialState';

export interface CommonAction {
  /**
   * Set APP title bar right component
   */
  setAppTitleBarRightComponent: (data: CommonState['appTitleBarRightComponent']) => void;
  /**
   * Set main page active tab
   */
  setMainPageActiveTab: (data: CommonState['mainPageActiveTab']) => void;
}

export const createCommonAction: StateCreator<GlobalStore, [['zustand/devtools', never]], [], CommonAction> = (
  set,
) => ({
  setAppTitleBarRightComponent: (data) => {
    set({
      appTitleBarRightComponent: data,
    });
  },
  setMainPageActiveTab: (data) => {
    set({
      mainPageActiveTab: data,
    });
  },
});
