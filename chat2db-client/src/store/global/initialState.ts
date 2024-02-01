import { CommonState, initialCommonState } from './slices/common/initialState';
import { GlobalSettingState, initialSettingState } from './slices/settings/initialState';

export type GlobalState = CommonState & GlobalSettingState;

export const initialState: GlobalState = {
  ...initialCommonState,
  ...initialSettingState,
};
