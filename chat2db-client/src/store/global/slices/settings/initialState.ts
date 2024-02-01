import { IAiConfig } from '@/typings/setting';
import { IRemainingUse, AIType } from '@/typings/ai';

export interface SettingState {
  /**
   *  APP title bar right component
   */
  aiConfig: IAiConfig;
  remainingUse?: IRemainingUse;
  hasWhite: boolean;
  holdingService: boolean;
}

export const initialSettingState: SettingState = {
  remainingUse: undefined,
  aiConfig: {
    aiSqlSource: AIType.CHAT2DBAI,
  },
  hasWhite: false,
  holdingService: false,
};
