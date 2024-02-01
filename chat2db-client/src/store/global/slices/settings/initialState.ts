import { GlobalSettings } from '@/typings/settings';
import { DEFAULT_AI_SETTINGS, DEFAULT_BASE_SETTINGS, DEFAULT_SERVER_SETTINGS } from '@/constants/settings';

export interface GlobalSettingState extends GlobalSettings {}

export const initialSettingState: GlobalSettingState = {
  baseSetting: DEFAULT_BASE_SETTINGS,
  aiSettings: DEFAULT_AI_SETTINGS,
  serverSettings: DEFAULT_SERVER_SETTINGS,
};
