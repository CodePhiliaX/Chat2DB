import createRequest from './base';

export interface ITask {
  id: number;
  gmtCreate: Date;
  gmtModified: Date;
  dataSourceId: number;
  databaseName: string;
  schemaName: string;
  tableName: string;
  deleted: string;
  userId: number;
  taskType: string;
  taskStatus: string;
  taskProgress: string;
  taskName: string;
  downloadUrl: string;
  content?: string;
}

export interface IExportResultDataParams {
  sql: string;
  originalSql: string;
  exportType: string;
  exportSize: string;
  dataSourceId?: number;
  databaseName?: string;
  schemaName?: string;
}

export interface IImportDataParams {
  file: File;
  tableName: string;
  fileType: string;
  dataSourceId?: number;
  databaseName?: string;
  schemaName?: string;
  fieldMappings?: string;
  importMode?: string;
}

export interface IPreviewHeadersParams {
  file: File;
  tableName: string;
  fileType: string;
  dataSourceId?: number;
  databaseName?: string;
  schemaName?: string;
}

export interface IExecuteSqlFileParams {
  file: File;
  dataSourceId?: number;
  databaseName?: string;
  schemaName?: string;
}

export interface ITableColumnInfo {
  name: string;
  type: string;
  primaryKey: boolean;
}

export interface IAutoMapping {
  sourceField: string;
  targetField: string;
  matched: boolean;
}

export interface IPreviewHeadersResult {
  fileHeaders: string[];
  tableColumns: ITableColumnInfo[];
  autoMappings: IAutoMapping[];
}

export interface IExportSchemaDocParams {
  exportType: string;
  exportSize: string;
  dataSourceId?: number;
  databaseName?: string;
  schemaName?: string;
  refresh?: boolean;
}

export interface IDataTransferParams {
  sourceDataSourceId: number;
  sourceDatabaseName?: string;
  sourceSchemaName?: string;
  targetDataSourceId: number;
  targetDatabaseName?: string;
  targetSchemaName?: string;
  tableNames: string[];
}

const exportResultData = createRequest<IExportResultDataParams, number>('/api/export/export_data', { method: 'post' });
const exportSchemaDoc = createRequest<IExportSchemaDocParams, number>('/api/export/export_doc', { method: 'post' });
const transferData = createRequest<IDataTransferParams, number>('/api/transfer/data', { method: 'post' });
const getTask = createRequest<{ id: number }, ITask>('/api/task/get/:id', { method: 'get' });
const getTaskList = createRequest<Record<string, never>, ITask[]>('/api/task/list', { method: 'get' });
const cleanupTasks = createRequest<Record<string, never>, number>('/api/task/cleanup', { method: 'post' });

const previewFileHeaders = (params: IPreviewHeadersParams): Promise<IPreviewHeadersResult> => {
  const { file, ...restParams } = params;
  const formData = new FormData();
  formData.append('file', file);
  Object.keys(restParams).forEach((key) => {
    const value = (restParams as any)[key];
    if (value !== undefined && value !== null) {
      formData.append(key, String(value));
    }
  });

  return fetch('/api/import/preview_headers', {
    method: 'POST',
    credentials: 'include',
    body: formData,
  })
    .then((res) => res.json())
    .then((res) => {
      if (res.success) {
        return res.data;
      }
      throw new Error(res.errorMessage || 'Preview failed');
    });
};

const importData = (params: IImportDataParams): Promise<number> => {
  const { file, ...restParams } = params;
  const formData = new FormData();
  formData.append('file', file);
  Object.keys(restParams).forEach((key) => {
    const value = (restParams as any)[key];
    if (value !== undefined && value !== null) {
      formData.append(key, String(value));
    }
  });

  return fetch('/api/import/import_data', {
    method: 'POST',
    credentials: 'include',
    body: formData,
  })
    .then((res) => res.json())
    .then((res) => {
      if (res.success) {
        return res.data;
      }
      throw new Error(res.errorMessage || 'Import failed');
    });
};

const executeSqlFile = (params: IExecuteSqlFileParams): Promise<number> => {
  const { file, ...restParams } = params;
  const formData = new FormData();
  formData.append('file', file);
  Object.keys(restParams).forEach((key) => {
    const value = (restParams as any)[key];
    if (value !== undefined && value !== null) {
      formData.append(key, String(value));
    }
  });

  return fetch('/api/sql/execute_file', {
    method: 'POST',
    credentials: 'include',
    body: formData,
  })
    .then((res) => res.json())
    .then((res) => {
      if (res.success) {
        return res.data;
      }
      throw new Error(res.errorMessage || 'Execute SQL failed');
    });
};

export default {
  exportResultData,
  exportSchemaDoc,
  importData,
  executeSqlFile,
  getTask,
  getTaskList,
  cleanupTasks,
  previewFileHeaders,
  transferData,
};
