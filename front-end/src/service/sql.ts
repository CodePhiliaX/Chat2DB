import createRequest from "./base";
import { IPageResponse, ITable, IPageParams } from '@/types';
import { DatabaseTypeCode } from '@/constants/database';

export interface IGetListParams extends IPageParams  {
  dataSourceId: number;
  databaseName: string;
  schemaName?: string;
}

export interface IExecuteSqlParams {
  sql: string,
  dataSourceId: number,
  databaseName: string,
  consoleId: number,
}

export interface IExecuteSqlResponse {
  sql: string;
  description: string;
  message: string;
  success: boolean;
  headerList:any[];
  dataList: any[];
}
export interface IConnectConsoleParams {
  consoleId: number,	
  dataSourceId: number,
  databaseName: string,
}

const getList = createRequest<IGetListParams, IPageResponse<ITable>>('/api/rdb/ddl/list',{});

const executeSql = createRequest<IExecuteSqlParams, IExecuteSqlResponse>('/api/rdb/dml/execute',{method: 'post'});

const connectConsole = createRequest<IConnectConsoleParams, void>('/api/connection/console/connect',{method: 'get'});

//表操作
export interface ITableParams {
  tableName:string;
  dataSourceId:number;	
  databaseName: string;
  schemaName?: string;
}

export interface IExecuteTableParams {
  sql: string;
  consoleId: number;	
  dataSourceId: number;
  databaseName: string;
}

export interface IColumn {
  name: string;
  dataType: string;
  columnType: string; // 列的类型 比如 varchar(100) ,double(10,6)
  nullable: boolean;
  primaryKey: boolean;
  defaultValue: string;
  autoIncrement: boolean;
  numericPrecision: number;
  numericScale: number;
  characterMaximumLength: number;
}

export interface ISchemaParams {
  dataSourceId: number;
  databaseName: string;
}
export interface ISchemaResponse {
  name: string;
}

const deleteTable = createRequest<ITableParams, void>('/api/rdb/ddl/delete',{method: 'post'});
const createTableExample = createRequest<{dbType:DatabaseTypeCode}, string>('/api/rdb/ddl/create/example',{method: 'get'});
const updateTableExample = createRequest<{dbType:DatabaseTypeCode}, string>('/api/rdb/ddl/update/example',{method: 'get'});
const exportCreateTableSql = createRequest<ITableParams, string>('/api/rdb/ddl/export',{method: 'get'});
const executeTable = createRequest<IExecuteTableParams, string>('/api/rdb/ddl/execute',{method: 'post'});

const getColumnList = createRequest<ITableParams, IColumn[]>('/api/rdb/ddl/column_list',{method: 'get'});
const getIndexList = createRequest<ITableParams, IColumn[]>('/api/rdb/ddl/index_list',{method: 'get'});
const getKeyList = createRequest<ITableParams, IColumn[]>('/api/rdb/ddl/key_list',{method: 'get'});
const getSchemaList = createRequest<ISchemaParams, ISchemaResponse[]>('/api/rdb/ddl/schema_list',{method: 'get'});


export default {
  getList,
  executeSql,
  connectConsole,
  deleteTable,
  createTableExample,
  updateTableExample,
  exportCreateTableSql,
  executeTable,
  getColumnList,
  getIndexList,
  getKeyList,
  getSchemaList
}