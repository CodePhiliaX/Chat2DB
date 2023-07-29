import { AiSqlSourceType } from './ai';

export interface IAiConfig {
  aiSqlSource: AiSqlSourceType;
  apiKey?: string;
  apiHost?: string;
  httpProxyHost?: string;
  httpProxyPort?: string;
  stream?: boolean;
  model?: string;
}
