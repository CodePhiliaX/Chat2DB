import { DatabaseTypeCode } from '@/constants';

// 连接 高级配置列表的信息
export interface IConnectionExtendInfoItem {
  key: string;
  value: string;
}

// 连接的环境信息
export interface IConnectionEnv {
  id: number;
  name: string;
  shortName: string;
  color: string;
}

// 连接列表的信息
export interface IConnectionListItem {
  id: number;
  alias: string;
  environment: IConnectionEnv;
  type: DatabaseTypeCode;
  supportDatabase: boolean;
  supportSchema: boolean;
  user: string;
}


export interface IConnectionDetails {
  id: number;
  alias: string;
  environment: IConnectionEnv;
  type: DatabaseTypeCode;

  isAdmin: boolean;
  url: string;
  user: string;
  password: string;
  ConsoleOpenedStatus: 'y' | 'n';
  extendInfo: IConnectionExtendInfoItem[];
  environmentId: number;
  ssh: any;
  driverConfig: {
    jdbcDriver: string;
    jdbcDriverClass: string;
  };
  [key: string]: any;
}

export interface IConnectionListItem {
  id: number;
  alias: string;
  environment: IConnectionEnv;
  type: DatabaseTypeCode;
  supportDatabase: boolean; 
  supportSchema: boolean;
  user: string;
}

export type ICreateConnectionDetails = Omit<IConnectionDetails, 'id'>

