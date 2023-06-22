import {
  EnvType,
  DatabaseTypeCode,
  TreeNodeType,
  WindowTabStatus,
  TableDataType,
  ConsoleType,
} from '@/utils/constants';

export interface IDatabase {
  name: string;
  code: DatabaseTypeCode;
  img: string;
  port: number;
  icon: string;
}
export interface IPageResponse<T> {
  data: T[];
  pageNo: number;
  pageSize: number;
  total: number;
  hasNextPage?: boolean;
}

export interface IPageParams {
  searchKey?: string;
  pageNo: number;
  pageSize: number;
}
export interface IConnectionBase {
  id?: number;
  alias: string;
  url: string;
  user: string;
  password: string;
  type: DatabaseTypeCode;
  tabOpened: 'y' | 'n';
  EnvType: EnvType;
}
export interface IHistoryRecord {
  id?: string | number;
  name: string;
  dataSourceId: string | number;
  databaseName: string;
  type: DatabaseTypeCode;
  dataSourceName: string;
  ddl: string;
  connectable: boolean;
}
export interface ITableColumn {
  name: string;
  type: string;
  description: string;
}
export interface ITableIndex {
  name: string;
  type: string;
  columns: string;
}
export interface ITable {
  name: string;
  description: string;
  columnList: ITableColumn[];
  indexList: ITableIndex[];
}
export interface ITreeNode {
  key: string;
  name: string;
  nodeType: TreeNodeType;
  dataType?: string;
  databaseType?: DatabaseTypeCode;
  isLeaf?: boolean;
  children?: ITreeNode[];
  parent?: ITreeNode;
  dataSourceId?: number;
  dataSourceName?: string;
  databaseName?: string;
  tableName?: string;
  schemaName?: string;
  columnType?: string;
  pretendNodeType?: TreeNodeType;
}
export interface IDB {
  name: string;
  description: string;
  count: number;
  databaseType?: DatabaseTypeCode;
}
export interface IWindowTab {
  id?: string;
  name: string;
  DBType: DatabaseTypeCode;
  dataSourceId: string | number;
  databaseName: string;
  consoleId?: string;
  status?: WindowTabStatus;
  ddl?: string;
  sql?: string;
}

export interface ITableHeaderItem {
  type: TableDataType;
  stringValue: string;
}

export interface ITableCellItem {
  type: TableDataType;
  stringValue: string;
  dateValue: number;
  byteValue: number[];
  emptyValue: any;
  bigDecimalValue: string;
}

export interface IManageResultData {
  dataList: ITableCellItem[][];
  headerList: ITableHeaderItem[];
  description: string;
  message: string;
  sql: string;
  success: boolean;
}
export interface IOptions {
  value: string | number;
  label: string;
  [key: string]: any;
}

export interface IConsoleBasic {
  name: string; // 名称
  key: string; // key 唯一
  type: ConsoleType; // 类型
  DBType: DatabaseTypeCode; // 数据库类型
  databaseName: string; // 数据库名称
  dataSourceId: number; // 数据源id
  dataSourceName?: string; // 数据源名称
  schemaName?: string;
}

// 查询sql的控制台
export interface ISQLQueryConsole extends IConsoleBasic {
  consoleId: number; // 与后端建立连接的控制台id
  status?: WindowTabStatus;
  ddl: string;
}

//编辑表结构的控制台
export interface IEditTableConsole extends IConsoleBasic {
  tableData?: ITreeNode;
}

export type IConsole = IEditTableConsole | ISQLQueryConsole;

export interface ISavedConsole {
  id: number;
  name: string;
  ddl: string;
  dataSourceId: number;
  databaseName: string;
  dataSourceName: string;
  schemaName: string;
  databaseType: DatabaseTypeCode;
  status: string;
  connectable: boolean;
}
