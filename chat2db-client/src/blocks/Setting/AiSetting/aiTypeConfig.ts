import { IAiConfig } from '@/typings';
import { AIType } from '@/typings/ai';

export type IAiConfigBooleans = {
  [K in keyof IAiConfig]?: boolean | string;
};

const formConfig: Record<AIType, IAiConfigBooleans> = {
  [AIType.CHAT2DBAI]: {
    apiKey: true,
  },
  [AIType.ZHIPUAI]: {
    apiKey: true,
    apiHost: true,
    model: true,
  },
  [AIType.BAICHUANAI]: {
    apiKey: true,
    secretKey: true,
    apiHost: true,
    model: true,
  },
  [AIType.OPENAI]: {
    apiKey: true,
    apiHost: true,
    httpProxyHost: true,
    httpProxyPort: true,
    // model: 'gpt-3.5-turbo',
  },
  [AIType.AZUREAI]: {
    apiKey: true,
    apiHost: true,
    model: true,
  },
  [AIType.RESTAI]: {
    apiKey: true,
    apiHost: true,
    model: true,
  },
};

export { formConfig };
