import { AIType } from '@/typings/ai';
import { GlobalAISettings, GlobalBaseSettings, GlobalServerSettings } from '@/typings/settings';

export const DEFAULT_BASE_SETTINGS: GlobalBaseSettings = {
  themeMode: 'auto',
  language: 'zh-CN',
  fontSize: 14,
};

export const DEFAULT_AI_SETTINGS: GlobalAISettings = {
  remainingUse: undefined,
  aiConfig: {
    aiSqlSource: AIType.CHAT2DBAI,
  },
  hasWhite: false,
};

export const DEFAULT_SERVER_SETTINGS: GlobalServerSettings = {
  holdingService: false,
  serviceAddress: 'http://127.0.0.1:10824',
};
