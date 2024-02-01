import { deepMerge } from '@/utils/merge';
import { GlobalStore } from '../../store';
import { DEFAULT_AI_SETTINGS, DEFAULT_BASE_SETTINGS, DEFAULT_SERVER_SETTINGS } from '@/constants/settings';
import { GlobalBaseSettings } from '@/typings';

const currentBaseSetting = (state: GlobalStore) => deepMerge(DEFAULT_BASE_SETTINGS, state.baseSetting);
const currentAISetting = (state: GlobalStore) => deepMerge(DEFAULT_AI_SETTINGS, state.aiSettings);
const currentServerSetting = (state: GlobalStore) => deepMerge(DEFAULT_SERVER_SETTINGS, state.serverSettings);

export const settingSelectors = {
  currentBaseSetting,
  currentAISetting,
  currentServerSetting,
};
