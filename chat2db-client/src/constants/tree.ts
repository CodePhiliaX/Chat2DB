export enum TreeNodeType {
  DATA_SOURCES = 'dataSources',
  DATA_SOURCE = 'dataSource',
  DATABASE = 'database',
  SCHEMAS = 'schemas',
  TABLES = 'tables',
  TABLE = 'table',
  COLUMNS = 'columns',
  COLUMN = 'column',
  KEYS = 'keys',
  KEY = 'key',
  INDEXES = 'indexes',
  INDEX = 'index',
  VIEWS = 'views', // 视图组
  VIEW = 'view', // 视图
  VIEWCOLUMN = 'viewColumn',
  VIEWCOLUMNS = 'viewColumns',
  FUNCTIONS = 'functions', // 函数组
  FUNCTION = 'function', // 函数
  PROCEDURES = 'procedures', // procedure组
  PROCEDURE = 'procedure', // procedure
  TRIGGERS = 'triggers',  // trigger组
  TRIGGER = 'trigger',  // trigger
}

// 树右键支持的功能
export enum OperationColumn {
  ShiftOut = 'shiftOut', // 移出数据源
  Refresh = 'refresh', // 刷新各级菜单
  CreateTable = 'createTable', //创建表
  CreateConsole = 'createConsole', // 新建console
  DeleteTable = 'deleteTable', // 删除表
  ViewDDL = 'viewDDL', // 导出ddl
  EditSource = 'editSource', // 编辑数据源
  Top = 'top', // 置顶
  EditTable = 'editTable', // 编辑表
  EditTableData = 'editTableData', // 编辑表数据
  CopyName = 'copyName', // 复制名称
}
