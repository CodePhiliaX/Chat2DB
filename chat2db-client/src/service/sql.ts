import createRequest from './base';
import {
  IPageResponse,
  IPageParams,
  IUniversalTableParams,
  IManageResultData,
  IRoutines,
  IDatabaseSupportField,
  IEditTableInfo,
  ITable,
} from '@/typings';
import { DatabaseTypeCode } from '@/constants';
import { ExportSizeEnum, ExportTypeEnum } from '@/typings/resultTable';

export interface IGetTableListParams extends IPageParams {
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
  schemaName?: string | null;
  tableName?: string;
  pageNo?: number;
  pageSize?: number;
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

const getTableList = createRequest<IGetTableListParams, IPageResponse<ITable>>('/api/rdb/table/list', { method: 'get' });

const executeSql = createRequest<IExecuteSqlParams, IManageResultData[]>('/api/rdb/dml/execute', { method: 'post', delayTime: 10 });

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

const getColumnList = createRequest<ITableParams, IColumn[]>('/api/rdb/ddl/column_list', {
  method: 'get',
  delayTime: 200,
});
const getIndexList = createRequest<ITableParams, IColumn[]>('/api/rdb/ddl/index_list', {
  method: 'get',
  delayTime: 200,
});
const getKeyList = createRequest<ITableParams, IColumn[]>('/api/rdb/ddl/key_list', { method: 'get', delayTime: 200 });
const getSchemaList = createRequest<ISchemaParams, ISchemaResponse[]>('/api/rdb/ddl/schema_list', {
  method: 'get',
  delayTime: 200,
});

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
const getViewList = createRequest<IGetTableListParams, IPageResponse<IRoutines>>('/api/rdb/view/list', {
  method: 'get',
});

/** 获取函数列表 */
const getFunctionList = createRequest<IGetTableListParams, IPageResponse<IRoutines>>('/api/rdb/function/list', {
  method: 'get',
});

/** 获取触发器列表 */
const getTriggerList = createRequest<IGetTableListParams, IPageResponse<IRoutines>>('/api/rdb/trigger/list', {
  method: 'get',
});

/** 获取过程列表 */
const getProcedureList = createRequest<IGetTableListParams, IPageResponse<IRoutines>>('/api/rdb/procedure/list', {
  method: 'get',
});

/** 获取视图列列表 */
const getViewColumnList = createRequest<IGetTableListParams, IPageResponse<IRoutines>>('/api/rdb/view/column_list', {
  method: 'get',
});

/** 获取视图详情 */
const getViewDetail = createRequest<
  {
    dataSourceId: number;
    databaseName: string;
    schemaName?: string;
    tableName: string;
  },
  { ddl: string }
>('/api/rdb/view/detail', { method: 'get' });

/** 获取触发器详情 */
const getTriggerDetail = createRequest<
  {
    dataSourceId: number;
    databaseName: string;
    schemaName?: string;
    triggerName: string;
  },
  { triggerBody: string }
>('/api/rdb/trigger/detail', { method: 'get' });

/** 获取函数详情 */
const getFunctionDetail = createRequest<
  {
    dataSourceId: number;
    databaseName: string;
    schemaName?: string;
    functionName: string;
  },
  { functionBody: string }
>('/api/rdb/function/detail', { method: 'get' });

/** 获取过程详情 */
const getProcedureDetail = createRequest<
  {
    dataSourceId: number;
    databaseName: string;
    schemaName?: string;
    procedureName: string;
  },
  { procedureBody: string }
>('/api/rdb/procedure/detail', { method: 'get' });

/** 格式化sql */
const sqlFormat = createRequest<
  {
    sql: string;
    dbType: DatabaseTypeCode;
  },
  string
>('/api/sql/format', { method: 'get' });

/** 数据库支持的数据类型 */
const getDatabaseFieldTypeList = createRequest<
  {
    dataSourceId: number;
    databaseName: string;
  },
  IDatabaseSupportField
>('/api/rdb/table/table_meta', { method: 'get' });

/** 获取表的详情 */
const getTableDetails = createRequest<
  {
    dataSourceId: number;
    databaseName: string;
    schemaName?: string | null;
    tableName: string;
    refresh: boolean;
  },
  IEditTableInfo
>('/api/rdb/table/query', { method: 'get' });

/** 获取库的所有表 */
const getAllTableList = createRequest<
  { dataSourceId: number; databaseName?: string | null; schemaName?: string | null },
  Array<{ name: string; comment: string }>
>('/api/rdb/table/table_list', { method: 'get' });

/** 获取表的所有字段 */
const getAllFieldByTable = createRequest<
  { dataSourceId: number; databaseName?: string; schemaName?: string | null; tableName: string },
  Array<{ name: string; tableName: string }>
>('/api/rdb/table/column_list', { method: 'get' });

export interface IModifyTableSqlParams {
  dataSourceId: number;
  databaseName: string;
  schemaName?: string | null;
  tableName?: string;
  oldTable?: IEditTableInfo;
  newTable: IEditTableInfo;
  refresh: boolean;
}

/** 获取修改表的sql */
const getModifyTableSql = createRequest<IModifyTableSqlParams, { sql: string }[]>('/api/rdb/table/modify/sql', {
  method: 'post',
});

/** 执行编辑表的sql, 专为编辑表而生 */
const executeDDL = createRequest<IExecuteSqlParams, { success: boolean; message: string; originalSql: string }>(
  '/api/rdb/dml/execute_ddl',
  { method: 'post' },
);

// 执行修改表数据的sql
const executeUpdateDataSql = createRequest<IExecuteSqlParams, { success: boolean; message: string; sql: string }>(
  '/api/rdb/dml/execute_update',
  { method: 'post' },
);

/** 获取修改表数据的接口 */
const getExecuteUpdateSql = createRequest<any, string>('/api/rdb/dml/get_update_sql', { method: 'post' });

/** 创建数据库  */ 
const getCreateDatabaseSql = createRequest<{
  dataSourceId: number;
  databaseName: string;
}, { sql: string }>('/api/rdb/database/create_database_sql', { method: 'post' });

/** 创建schema  */ 
const getCreateSchemaSql = createRequest<{
  dataSourceId: number;
  databaseName?: string;
  schemaName?: string;
}, {sql:string}>('/api/rdb/schema/create_schema_sql', { method: 'post' });

export default {
  getCreateSchemaSql,
  getCreateDatabaseSql,
  executeUpdateDataSql,
  executeDDL,
  getExecuteUpdateSql,
  getModifyTableSql,
  getTableDetails,
  getDatabaseFieldTypeList,
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
  getTableList,
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
  getAllTableList,
  getAllFieldByTable,
};
