import type { StateCreator } from 'zustand/vanilla';
import { GlobalStore } from '../../store';
import configService from '@/service/config';
import aiService from '@/service/ai';
import { AIType, IAIConfig, IRemainingUse } from '@/typings/ai';
import i18n from '@/i18n';
import { GlobalBaseSettings, GlobalAISettings } from '@/typings/settings';
import { DeepPartial } from 'utility-types';
import { deepMerge } from '@/utils/merge';
import { deepEqual } from '@/utils/equal';
import { produce } from 'immer';
import { staticMessage as message } from '@chat2db/ui';

export interface SettingsAction {
  // ====================== BaseSetting ======================
  /**
   * 设置基础配置
   */
  setBaseSetting: (setting: DeepPartial<GlobalBaseSettings>) => void;
  /**
   * 设置主题模式
   */
  setThemeMode: (themeMode: GlobalBaseSettings['themeMode']) => void;
  /**
   * 设置主题颜色
   */
  setPrimaryColor: (primaryColor: GlobalBaseSettings['primaryColor']) => void;
  /**
   * 设置中性色
   */
  setNeutralColor: (neutralColor: GlobalBaseSettings['neutralColor']) => void;
  /**
   * 设置语言
   */
  setLanguage: (language: GlobalBaseSettings['language']) => void;

  // ====================== AISetting ======================
  /**
   * 设置AI配置
   */
  setAISetting: (settings: DeepPartial<GlobalAISettings>) => void;
  /**
   * 设置AI 模型相关配置
   */
  setAIConfig: (aiConfig: Partial<IAIConfig>) => void;
  /**
   * 设置用户剩余次数相关配置
   */
  setRemainUse: (remainingUse?: Partial<IRemainingUse>) => void;
  /**
   * 设置是否开启白名单
   */
  setAIWithWhite: (hasWhite: boolean) => void;
  /**
   * 更新AI模型配置
   */
  updateAIConfig: (aiConfig: Partial<IAIConfig>) => void;
  /**
   * 更新用户剩余次数配置
   */
  updateRemainingUse: (apiKey?: string) => void;
  /**
   * 更新AI白名单
   */
  updateAIWithWhite: (apiKey?: string) => void;
}

export const createSettingsAction: StateCreator<GlobalStore, [['zustand/devtools', never]], [], SettingsAction> = (
  set,
  get,
) => ({
  // ====================== BaseSetting ======================
  setBaseSetting: (baseSetting) => {
    set({
      baseSetting: produce(get().baseSetting, (draft) => {
        Object.assign(draft, baseSetting);
      }),
    });
  },
  setThemeMode: (themeMode) => {
    get().setBaseSetting({ themeMode });
  },
  setPrimaryColor: (primaryColor) => {
    get().setBaseSetting({ primaryColor });
  },
  setNeutralColor: (neutralColor) => {
    get().setBaseSetting({ neutralColor });
  },
  setLanguage: (language) => {
    get().setBaseSetting({ language });
  },

  // ====================== AISetting ======================
  setAISetting: (aiSettings) => {
    const prevAISetting = get().aiSettings;
    const nextAISetting = deepMerge(prevAISetting, aiSettings) as GlobalAISettings;
    if (deepEqual(prevAISetting, nextAISetting)) return;
    set({
      aiSettings: nextAISetting,
    });
  },
  setAIConfig: (aiConfig) => {
    get().setAISetting({ aiConfig });
  },
  setRemainUse: (remainingUse) => {
    get().setAISetting({ remainingUse });
  },
  setAIWithWhite: (hasWhite) => {
    get().setAISetting({ hasWhite });
  },
  updateAIConfig: (aiConfig) => {
    configService.setAISystemConfig(aiConfig).then(() => {
      message.success(i18n('common.text.submittedSuccessfully'));
      get().setAIConfig(aiConfig);
    });
    if (aiConfig?.aiSqlSource === AIType.CHAT2DBAI) {
      get().updateAIWithWhite(aiConfig?.apiKey);
    } else {
      get().setAIWithWhite(false);
    }
  },
  updateAIWithWhite: (apiKey) => {
    configService.getAIWhiteAccess({ apiKey: apiKey ?? '' }).then((hasWhite) => {
      get().setAIWithWhite(hasWhite);
    });
  },
  updateRemainingUse: (apiKey) => {
    const aiSqlSource = get().aiSettings.aiConfig.aiSqlSource;
    if (!apiKey || aiSqlSource !== AIType.CHAT2DBAI) {
      get().setRemainUse(undefined);
      return;
    }
    aiService.getRemainingUse().then((res) => {
      get().setRemainUse(res);
    });
  },
});
