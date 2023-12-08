import { ConsoleStatus, DatabaseTypeCode, WorkspaceTabType, ConsoleOpenedStatus } from '@/constants';

export interface ICreateConsoleParams { 
  name?: string;
  ddl?: string;
  dataSourceId: number;
  dataSourceName: string;
  databaseType: DatabaseTypeCode;
  databaseName?: string;
  schemaName?: string;
  operationType?: WorkspaceTabType;
  loadSQL: () => Promise<string>;
}

// 控制台详情
export interface IConsole {
  id: number; // consoleId
  name: string; // 控制台名称
  ddl: string; // 控制台内的sql
  dataSourceId?: number; // 数据源id
  dataSourceName?: string; // 数据源名称
  type?: DatabaseTypeCode; // 数据库类型
  databaseName?: string; // 数据库名称
  schemaName?: string; // schema名称
  status: ConsoleStatus; // 控制台状态
  connectable: boolean; // 是否可连接
  tabOpened?: ConsoleOpenedStatus; // 控制台tab是否打开
  operationType: WorkspaceTabType; // 操作类型
}

export type ICreateConsole = Omit<IConsole, 'id' | 'dataSourceName' | 'connectable'>;

