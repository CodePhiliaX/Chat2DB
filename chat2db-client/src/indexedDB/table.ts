
export interface IWorkspaceConsoleDDL {
  consoleId: string; // 控制台的id 唯一
  ddl: string; // 数据源ddl
  userId?: string; // 用户的唯一id
}

// 工作区console表
export const workspaceConsoleDDL = {
  name: 'workspaceConsoleDDL',
  primaryKey: {
    keyPath: 'consoleId',
    autoIncrement: true,
  },
  column: [
    {
      name: 'consoleId',
      isIndex: true,
      keyPath: 'consoleId',
      options: {
        unique: true,
      },
    },
    {
      name: 'userId',
      isIndex: true,
      keyPath: 'userId',
      options: {
        unique: false,
      },
    },
    {
      name: 'ddl',
      isIndex: true,
      keyPath: 'ddl',
      options: {
        unique: false,
      },
    },

  ],
}

export const tableList = [
  {
    tableDetails: workspaceConsoleDDL,
  }
]
