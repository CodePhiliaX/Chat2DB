import { IAiConfig } from '@/typings';
import createRequest from './base';
const getSystemConfig = createRequest<{ code: string }, { code: string; content: string }>(
  '/api/config/system_config/:code',
  { errorLevel: false },
);
const setSystemConfig = createRequest<{ code: string; content: string }, void>('/api/config/system_config', {
  errorLevel: 'toast',
  method: 'post',
});

const getAiSystemConfig = createRequest<{ aiSqlSource?: string }, IAiConfig>('/api/config/system_config/ai', {
  errorLevel: false,
});

const setAiSystemConfig = createRequest<IAiConfig, void>('/api/config/system_config/ai', {
  errorLevel: 'toast',
  method: 'post',
});

const getAiWhiteAccess = createRequest<{ apiKey: string }, boolean>('/api/ai/embedding/white/check', {
  method: 'get',
});

export default {
  getSystemConfig,
  setSystemConfig,
  getAiSystemConfig,
  setAiSystemConfig,
  getAiWhiteAccess
};
