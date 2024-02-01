export interface CommonState {
  /**
   *  APP title bar right component
   */
  appTitleBarRightComponent: React.ReactNode | null;
}

export const initialCommonState: CommonState = {
  appTitleBarRightComponent: null,
};
