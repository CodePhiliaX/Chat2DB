export interface CommonState {
  /**
   *  APP title bar right component
   */
  appTitleBarRightComponent: React.ReactNode | null;
  /**
   * Main page active tab
   */
  mainPageActiveTab: string;
}

export const initialCommonState: CommonState = {
  appTitleBarRightComponent: null,
  mainPageActiveTab: 'connection',
};
