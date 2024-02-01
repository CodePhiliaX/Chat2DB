import { NeutralColors, PrimaryColors } from '@chat2db/ui';
import { ThemeMode } from 'antd-style';
import { IAIConfig, IRemainingUse } from './ai';

export interface GlobalBaseSettings {
  themeMode: ThemeMode;
  primaryColor?: PrimaryColors;
  neutralColor?: NeutralColors;
  // TODO: Type definition to be improved
  language: string;
  fontSize: number;
}

export interface GlobalAISettings {
  /**
   * AI相关配置
   */
  aiConfig: IAIConfig;
  /**
   * AI剩余使用次数
   */
  remainingUse?: IRemainingUse;
  /**
   * 是否加入白名单
   */
  hasWhite: boolean;
}

export interface GlobalServerSettings {
  /**
   * 关闭APP是否不杀后端服务
   */
  holdingService: boolean;
  /**
   * 后端服务地址
   */
  serviceAddress: string;
}

export interface GlobalSettings {
  baseSetting: GlobalBaseSettings;
  aiSettings: GlobalAISettings;
  serverSettings: GlobalServerSettings;
}
