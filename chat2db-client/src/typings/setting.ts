import { AIType } from './ai';

export interface IAiConfig {
  aiSqlSource: AIType;
  apiKey?: string;
  apiHost?: string;
  httpProxyHost?: string;
  httpProxyPort?: string;
  stream?: boolean;
  secretKey?:string;
  model?: string;
}
