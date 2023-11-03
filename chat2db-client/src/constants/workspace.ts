export enum CreateTabIntroType {
  EditorTable = 'editorTable',
  EditTableData = 'editTableData',
}


// 工作台Tab的类型
export enum WorkspaceTabType {
  CONSOLE = 'console',
  FUNCTION = 'function',
  PROCEDURE = 'procedure',
  VIEW = 'view',
  TRIGGER = 'trigger',
  EditTable = 'editTable',
  CreateTable = 'createTable',
  EditTableData = 'editTableData',
}

// 工作台Tab的类型对应的一些配置
export const workspaceTabConfig: {
  [key in WorkspaceTabType]: {
    icon: string
  };
} = {
  [WorkspaceTabType.CONSOLE]: {
    icon: '\uec83'
  },
  [WorkspaceTabType.VIEW]: {
    icon: '\ue70c'
  },
  [WorkspaceTabType.FUNCTION]: {
    icon: '\ue76a'
  },
  [WorkspaceTabType.PROCEDURE]: {
    icon: '\ue73c'
  },
  [WorkspaceTabType.TRIGGER]: {
    icon: '\ue64a'
  },
  [WorkspaceTabType.EditTable]: {
    icon: '\ue6f3'
  },
  [WorkspaceTabType.CreateTable]: {
    icon: '\ue6b6'
  },
  [WorkspaceTabType.EditTableData]: {
    icon: '\ue618'
  }
}

