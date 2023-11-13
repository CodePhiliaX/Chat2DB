import { ConsoleStatus, DatabaseTypeCode, WorkspaceTabType } from '@/constants';

// 控制台详情
export interface IConsole {
  id: number; // consoleId
  name: string; // 控制台名称
  ddl: string; // 控制台内的sql
  dataSourceId: number; // 数据源id
  dataSourceName: string; // 数据源名称
  databaseName?: string; // 数据库名称
  schemaName?: string; // schema名称
  type: DatabaseTypeCode; // 数据库类型
  status: ConsoleStatus; // 控制台状态
  connectable: boolean; // 是否可连接
  tabOpened?: 'y' | 'n'; // 控制台tab是否打开
  operationType: WorkspaceTabType; // 操作类型
}
