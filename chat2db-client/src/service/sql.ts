import createRequest from './base';
import { IPageResponse, IPageParams, IUniversalTableParams, IManageResultData, IRoutines } from '@/typings';
import { DatabaseTypeCode } from '@/constants';
import { ExportSizeEnum, ExportTypeEnum } from '@/typings/resultTable';

export interface IGetListParams extends IPageParams {
  dataSourceId: number;
  databaseName: string;
  schemaName?: string;
  databaseType?: DatabaseTypeCode;
}

export interface IExecuteSqlParams {
  sql?: string;
  consoleId?: number;
  dataSourceId?: number;
  databaseName?: string;
  schemaName?: string;
}

export interface IExecuteSqlResponse {
  sql: string;
  description: string;
  message: string;
  success: boolean;
  headerList: any[];
  dataList: any[];
}
export interface IConnectConsoleParams {
  consoleId: number;
  dataSourceId: number;
  databaseName: string;
}

const getList = createRequest<IGetListParams, IPageResponse<ITable>>('/api/rdb/ddl/list', { method: 'get' });

const executeSql = createRequest<IExecuteSqlParams, IManageResultData[]>('/api/rdb/dml/execute', { method: 'post' });

const connectConsole = createRequest<IConnectConsoleParams, void>('/api/connection/console/connect', { method: 'get' });

//表操作
export interface ITableParams {
  tableName: string;
  dataSourceId: number;
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
  comment: string;
}

export interface ISchemaParams {
  dataSourceId: number;
  databaseName: string;
}
export interface ISchemaResponse {
  name: string;
}

export interface MetaSchemaVO {
  databases?: Database[];
  schemas?: Schema[];
}

export interface Database {
  name: string;
  schemas?: Schema[];
}

export interface Schema {
  name: string;
}

const deleteTable = createRequest<ITableParams, void>('/api/rdb/ddl/delete', { method: 'post' });
const createTableExample = createRequest<{ dbType: DatabaseTypeCode }, string>('/api/rdb/ddl/create/example', {
  method: 'get',
});
const updateTableExample = createRequest<{ dbType: DatabaseTypeCode }, string>('/api/rdb/ddl/update/example', {
  method: 'get',
});
const exportCreateTableSql = createRequest<ITableParams, string>('/api/rdb/ddl/export', { method: 'get' });
const executeTable = createRequest<IExecuteTableParams, string>('/api/rdb/ddl/execute', { method: 'post' });

const getColumnList = createRequest<ITableParams, IColumn[]>('/api/rdb/ddl/column_list', { method: 'get' });
const getIndexList = createRequest<ITableParams, IColumn[]>('/api/rdb/ddl/index_list', { method: 'get' });
const getKeyList = createRequest<ITableParams, IColumn[]>('/api/rdb/ddl/key_list', { method: 'get' });
const getSchemaList = createRequest<ISchemaParams, ISchemaResponse[]>('/api/rdb/ddl/schema_list', { method: 'get' });

const getDatabaseSchemaList = createRequest<{ dataSourceId: number }, MetaSchemaVO>(
  '/api/rdb/ddl/database_schema_list',
  { method: 'get' },
);

const addTablePin = createRequest<IUniversalTableParams, void>('/api/pin/table/add', { method: 'post' });

const deleteTablePin = createRequest<IUniversalTableParams, void>('/api/pin/table/delete', { method: 'post' });

/** 获取当前执行SQL 所有行 */
const getDMLCount = createRequest<IExecuteSqlParams, number>('/api/rdb/dml/count', { method: 'post' });

export interface IExportParams extends IExecuteSqlParams {
  originalSql: string;
  exportType: ExportTypeEnum;
  exportSize: ExportSizeEnum;
}
/**
 * 导出-表格
 */
// const exportResultTable = createRequest<IExportParams, any>('/api/rdb/dml/export', { method: 'post' });

/** 获取视图列表 */
const getViewList = createRequest<IGetListParams, IPageResponse<IRoutines>>('/api/rdb/view/list', { method: 'get' });

/** 获取函数列表 */
const getFunctionList = createRequest<IGetListParams, IPageResponse<IRoutines>>('/api/rdb/function/list', { method: 'get' });

/** 获取触发器列表 */
const getTriggerList = createRequest<IGetListParams, IPageResponse<IRoutines>>('/api/rdb/trigger/list', { method: 'get' });

/** 获取过程列表 */
const getProcedureList = createRequest<IGetListParams, IPageResponse<IRoutines>>('/api/rdb/procedure/list', { method: 'get' });

/** 获取视图列列表 */
const getViewColumnList = createRequest<IGetListParams, IPageResponse<IRoutines>>('/api/rdb/view/column_list', { method: 'get' });

/** 获取视图详情 */
const getViewDetail = createRequest<{
  dataSourceId: number;
  databaseName: string;
  schemaName?: string;
  tableName: string
}, {ddl: string}>('/api/rdb/view/detail', { method: 'get' });

/** 获取触发器详情 */
const getTriggerDetail = createRequest<{
  dataSourceId: number;
  databaseName: string;
  schemaName?: string;
  triggerName: string
}, {triggerBody: string}>('/api/rdb/trigger/detail', { method: 'get' });

/** 获取函数详情 */
const getFunctionDetail = createRequest<{
  dataSourceId: number;
  databaseName: string;
  schemaName?: string;
  functionName: string
}, {functionBody: string}>('/api/rdb/function/detail', { method: 'get' });

/** 获取过程详情 */
const getProcedureDetail = createRequest<{
  dataSourceId: number;
  databaseName: string;
  schemaName?: string;
  procedureName: string
}, { procedureBody: string }>('/api/rdb/procedure/detail', { method: 'get' });

/** 格式化sql */
const sqlFormat  = createRequest<{
  sql: string;
  dbType: DatabaseTypeCode;
}, string>('/api/sql/format', { method: 'get' });

export default {
  sqlFormat,
  getTriggerDetail,
  getProcedureDetail,
  getFunctionDetail,
  getViewDetail,
  getViewColumnList,
  getProcedureList,
  getTriggerList,
  getFunctionList,
  getViewList,
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
  getSchemaList,
  getDatabaseSchemaList,
  addTablePin,
  deleteTablePin,
  getDMLCount,
  // exportResultTable
};
