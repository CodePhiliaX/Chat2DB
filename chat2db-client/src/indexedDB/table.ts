import { WorkspaceTabType } from '@/constants';

export interface IWorkspaceConsole {
  id: number; // Tab的id
  name: string; // 工作区tab的名称
  dataSourceId: number; // 数据源id
  dataSourceName: string; // 数据源名称
  databaseName: string; // 数据库名称
  schemaName?: string; // schema名称q
  databaseType: string; // 数据源类型
  ddl: string; // 数据源ddl
  workspaceTabType: WorkspaceTabType; // 操作类型
  userName?: string; // 用户名，用户的唯一id
}

// 工作区console表
export const workspaceConsoleTable = {
  name: 'workspaceConsoleTab',
  primaryKey: {
    keyPath: 'id',
    autoIncrement: true,
  },
  column: [
    {
      name: 'id',
      keyPath: 'id',
      isIndex: true,
      options: {
        unique: true,
      },
    },
    {
      name: 'userName',
      keyPath: 'userName',
      isIndex: true,
      options: {
        unique: false,
      },
    },
    {
      name: 'name',
      keyPath: 'name',
      isIndex: true,
      options: {
        unique: false,
      },
    },
    {
      name: 'dataSourceId',
      keyPath: 'dataSourceId',
      isIndex: true,
      options: {
        unique: false,
      },
    },
    {
      name: 'dataSourceName',
      keyPath: 'dataSourceName',
      isIndex: true,
      options: {
        unique: false,
      },
    },
    {
      name: 'databaseName',
      keyPath: 'databaseName',
      isIndex: true,
      options: {
        unique: false,
      },
    },
    {
      name: 'schemaName',
      keyPath: 'schemaName',
      isIndex: true,
      options: {
        unique: false,
      },
    },
    {
      name: 'databaseType',
      keyPath: 'databaseType',
      isIndex: true,
      options: {
        unique: false,
      },
    },
    {
      name: 'ddl',
      keyPath: 'ddl',
      options: {
        unique: false,
      },
    },
    {
      name: 'workspaceTabType',
      keyPath: 'workspaceTabType',
      isIndex: true,
      options: {
        unique: false,
      },
    },
  ],
}

export const tableList = [
  {
    tableDetails: workspaceConsoleTable,
  }
]
