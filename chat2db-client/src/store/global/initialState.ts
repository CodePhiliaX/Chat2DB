import { CommonState, initialCommonState } from './slices/common/initialState';
import { SettingState, initialSettingState } from './slices/settings/initialState';

export type GlobalState = CommonState & SettingState;

export const initialState: GlobalState = {
  ...initialCommonState,
  ...initialSettingState,
};
