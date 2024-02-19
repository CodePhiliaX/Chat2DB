import { CommonState, initialCommonState } from './slices/common/initialState';
import { GlobalSettingState, initialSettingState } from './slices/settings/initialState';
import { RequestState, initialRequestState } from './slices/request/initialState';

export type GlobalState = CommonState & GlobalSettingState & RequestState;

export const initialState: GlobalState = {
  ...initialCommonState,
  ...initialSettingState,
  ...initialRequestState
};
