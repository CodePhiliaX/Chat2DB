import { IPageResponse, IConnectionDetails, ICreateConnectionDetails, IConnectionEnv, IPageParams, IConnectionListItem } from '@/typings';
import { DatabaseTypeCode } from '@/constants';
import createRequest from './base';

export interface IDriverResponse {
  driverConfigList: {
    jdbcDriver: string;
    jdbcDriverClass: string;
  }[];
  defaultDriverConfig: {
    jdbcDriverClass: string;
  };
}

interface IDriverParams {
  dbType: DatabaseTypeCode;
}

interface IUploadDriver {
  multipartFiles: any;
  jdbcDriverClass: string;
  dbType: string;
}

/**
 * 查询连接列表
 */
const getList = createRequest<IPageParams, IPageResponse<IConnectionListItem>>(
  '/api/connection/datasource/list',
  {},
);

const getDetails = createRequest<{ id: number }, IConnectionDetails>('/api/connection/datasource/:id', {});

const save = createRequest<ICreateConnectionDetails, number>('/api/connection/datasource/create', {
  method: 'post',
  delayTime: true,
});

const close = createRequest<IConnectionDetails, void>('/api/connection/datasource/close', { method: 'post' });

const test = createRequest<IConnectionDetails, boolean>('/api/connection/datasource/pre_connect', {
  method: 'post',
  delayTime: true,
});

const testSSH = createRequest<any, boolean>('/api/connection/ssh/pre_connect', {
  method: 'post',
  delayTime: true,
});

const update = createRequest<IConnectionDetails, void>('/api/connection/datasource/update', { method: 'post' });

const remove = createRequest<{ id: number }, void>('/api/connection/datasource/:id', { method: 'delete' });

const clone = createRequest<{ id: number }, void>('/api/connection/datasource/clone', { method: 'post' });

const getDBList = createRequest<{ dataSourceId: number; refresh?: boolean }, any>('/api/rdb/database/list', {
  method: 'get',
});

const getSchemaList = createRequest<{ dataSourceId: number; databaseName: string; refresh?: boolean }, any>(
  '/api/rdb/schema/list',
  { method: 'get' },
);

const getDriverList = createRequest<IDriverParams, IDriverResponse>('/api/jdbc/driver/list', {
  errorLevel: false,
  method: 'get',
});
const downloadDriver = createRequest<{ dbType: string }, void>('/api/jdbc/driver/download', {
  errorLevel: false,
  method: 'get',
});

const saveDriver = createRequest<IUploadDriver, void>('/api/jdbc/driver/save', { method: 'post' });

const getEnvList = createRequest<void, IConnectionEnv[]>('/api/common/environment/list_all', { errorLevel: false });

/** 导入Navicat链接 */
// const importNavicatConnection = createRequest<
//   {
//     formData: FormData;
//   },
//   void
// >('/api/converter/ncx/upload', {
//   method: 'post',
// });

export default {
  getEnvList,
  getList,
  getDetails,
  save,
  test,
  update,
  remove,
  clone,
  getDBList,
  getSchemaList,
  close,
  testSSH,
  getDriverList,
  downloadDriver,
  saveDriver,
  // importNavicatConnection,
};
