import sqlService, { MetaSchemaVO } from '@/service/sql';
import { DatabaseTypeCode } from '@/constants';
import { Effect, Reducer } from 'umi';

export type ICurWorkspaceData = {
  dataSourceId: number;
  databaseSourceName: string;
  databaseType: DatabaseTypeCode;
  databaseName?: string;
  schemaName?: string;
}

export interface IState {
  // 当前连接下的及联databaseAndSchema数据
  databaseAndSchema: MetaSchemaVO;
  // 当前工作区所需的参数
  curWorkspaceParams: ICurWorkspaceData;
}

export interface IWorkspaceModelType  {
  namespace: 'workspace',
  state: IState,
  reducers: {
    setDatabaseAndSchema: Reducer<IState['databaseAndSchema']>;
    setCurWorkspaceParams: Reducer<IState['curWorkspaceParams']>;
  };
  effects: {
    fetchdatabaseAndSchema: Effect;
  };
}

const WorkspaceModel:IWorkspaceModelType = {
  namespace: 'workspace',

  state: {
    databaseAndSchema: {},
    curWorkspaceParams: {} as ICurWorkspaceData
  },

  reducers: {
    // 设置 database schema 数据
    setDatabaseAndSchema(state, { payload }) {
      return {
        ...state,
        databaseAndSchema: payload,
      };
    },

    setCurWorkspaceParams(state, { payload }) {
      return {
        ...state,
        curWorkspaceParams: payload,
      };
    },
  },

  effects: {
    *fetchdatabaseAndSchema(p, action) {
      const { call, put } = action
      console.log(p,action)
      const res = (yield sqlService.getDatabaseSchemaList({ dataSourceId: 2 }))
      yield put({
        type: 'setDatabaseAndSchema',
        payload: res,
      });
    },
  },
};

export default WorkspaceModel
