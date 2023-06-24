import { DatabaseTypeCode } from '@/constants/database';
import { ConnectionEnv } from '@/constants/environment';

export interface IConnectionExtendInfoItem {
  key: string;
  value: string;
}

export interface IConnectionDetails {
  id: number;
  alias: string;
  url: string;
  user: string;
  password: string;
  type: DatabaseTypeCode;
  ConsoleOpenedStatus: 'y' | 'n';
  EnvType: ConnectionEnv;
  extendInfo: IConnectionExtendInfoItem[];
  ssh: any;
  [key: string]: any;
}

export type ICreateConnectionDetails = Omit<IConnectionDetails, 'id'>
