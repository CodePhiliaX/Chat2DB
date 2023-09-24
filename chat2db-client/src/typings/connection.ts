import { DatabaseTypeCode, ConnectionEnv } from '@/constants';

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
  environmentId: number;
  environment: IConnectionEnv,
  ssh: any;
  driverConfig: {
    jdbcDriver: string;
    jdbcDriverClass: string;
  };
  [key: string]: any;
}

export type ICreateConnectionDetails = Omit<IConnectionDetails, 'id'>

// Connected environment
export interface IConnectionEnv {
  id: number;
  name: string;
  shortName: string;
  color: string;
}
