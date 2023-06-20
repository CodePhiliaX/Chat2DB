export enum InputType {
  INPUT = 'input',
  PASSWORD = 'password',
  SELECT = 'select',
}

export enum AuthenticationType {
  USERANDPASSWORD = 1,
  NONE = 2,
}

export enum SSHAuthenticationType {
  PASSWORD = 1,
  KEYPAIR = 2,
  OPENSSH = 3
}

// 树右键支持的功能
export enum OperationColumn {
  ShiftOut = 'shiftOut', // 移出数据源
  REFRESH = 'refresh', // 刷新各级菜单
  CreateTable = 'createTable', //创建表
  CreateConsole = 'createConsole', // 新建console
  DeleteTable = 'deleteTable', // 删除表
  ExportDDL = 'exportDDL', // 导出ddl
  EditSource = 'editSource', // 编辑数据源
}
