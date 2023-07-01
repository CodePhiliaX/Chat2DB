import { AiSqlSourceType } from '@/typings/ai';
import createRequest from './base';
const getSystemConfig = createRequest<{ code: string }, { code: string; content: string }>(
  '/api/config/system_config/:code',
  { errorLevel: false },
);
const setSystemConfig = createRequest<{ code: string; content: string }, void>('/api/config/system_config', {
  errorLevel: 'toast',
  method: 'post',
});

export interface IChatGPTConfig {
  apiKey: string;
  httpProxyHost: string;
  httpProxyPort: string;
  restAiUrl: string;
  apiHost: string;
  aiSqlSource: AiSqlSourceType;
  restAiStream: boolean;
  azureEndpoint: string;
  azureApiKey: string;
  azureDeploymentId: string;
}
const getChatGptSystemConfig = createRequest<void, IChatGPTConfig>('/api/config/system_config/chatgpt', {
  errorLevel: false,
});

const setChatGptSystemConfig = createRequest<IChatGPTConfig, void>('/api/config/system_config/chatgpt', {
  errorLevel: 'toast',
  method: 'post',
});

export default {
  getSystemConfig,
  setSystemConfig,
  getChatGptSystemConfig,
  setChatGptSystemConfig,
};
