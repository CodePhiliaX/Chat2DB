import { DatabaseTypeCode } from '@/constants';

export interface ILegacyDialectCompletion {
  type: DatabaseTypeCode;
  keywords?: string[];
  functions?: string[];
}

export interface ISqlDialectCompletion {
  type: DatabaseTypeCode | 'GENERIC';
  keywords: string[];
  functions: string[];
  dataTypes: string[];
  variables: string[];
  operators: string[];
}
