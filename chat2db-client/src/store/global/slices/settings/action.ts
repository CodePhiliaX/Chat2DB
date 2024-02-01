import type { StateCreator } from 'zustand/vanilla';
import { GlobalStore } from '../../store';
import { SettingState } from './initialState';
import configService from '@/service/config';
import aiService from '@/service/ai';
import { AIType } from '@/typings/ai';
import { message } from 'antd';
import i18n from '@/i18n';

export interface SettingsAction {
  setAiConfig: (aiConfig: SettingState['aiConfig']) => void;
  setRemainUse: (remainingUse?: SettingState['remainingUse']) => void;
  setAiWithWhite: (hasWhite: boolean) => void;
  updateAiWithWhite: (apiKey?: string) => void;
  getAiSystemConfig: () => void;
  setAiSystemConfig: (aiConfig: SettingState['aiConfig']) => void;
  fetchRemainingUse: (apiKey?: string) => void;
  setHoldingService: (holdingService: boolean) => void;
}

export const createSettingsAction: StateCreator<GlobalStore, [['zustand/devtools', never]], [], SettingsAction> = (
  set,
  get,
) => ({
  setAiConfig: (aiConfig) => set({ aiConfig }),
  setRemainUse: (remainingUse) => set({ remainingUse }),
  setAiWithWhite: (hasWhite) => set({ hasWhite }),
  updateAiWithWhite: (apiKey) => {
    configService.getAiWhiteAccess({ apiKey: apiKey ?? '' }).then((res) => {
      get().setAiWithWhite(res);
    });
  },
  getAiSystemConfig: () => {
    configService.getAiSystemConfig({}).then((res) => {
      get().setAiConfig(res);
      if (res?.aiSqlSource === AIType.CHAT2DBAI && res.apiKey) {
        get().updateAiWithWhite(res.apiKey);
      }
    });
  },
  setAiSystemConfig: (aiConfig) => {
    configService.setAiSystemConfig(aiConfig).then(() => {
      message.success(i18n('common.text.submittedSuccessfully'));
      get().setAiConfig(aiConfig);
    });
    if (aiConfig?.aiSqlSource === AIType.CHAT2DBAI) {
      get().updateAiWithWhite(aiConfig?.apiKey);
    } else {
      get().setAiWithWhite(false);
    }
  },
  fetchRemainingUse: (apiKey) => {
    const aiSqlSource = get().aiConfig.aiSqlSource;
    if (!apiKey || aiSqlSource !== AIType.CHAT2DBAI) {
      get().setRemainUse(undefined);
      return;
    }
    aiService.getRemainingUse().then((res) => {
      get().setRemainUse(res);
    });
  },
  setHoldingService: (holdingService) => {
    set({ holdingService });
  },
});
