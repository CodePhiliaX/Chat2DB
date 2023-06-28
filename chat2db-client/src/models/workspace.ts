import { getCurrentWorkspaceDatabase, setCurrentWorkspaceDatabase } from '@/utils/localStorage';
import sqlService, { MetaSchemaVO } from '@/service/sql';
import { DatabaseTypeCode } from '@/constants';
import { Effect, Reducer } from 'umi';
import { ITreeNode } from '@/typings';

export type ICurWorkspaceParams = {
  dataSourceId: number;
  databaseSourceName: string;
  databaseType: DatabaseTypeCode;
  databaseName?: string;
  schemaName?: string;
};

export interface IState {
  // 当前连接下的及联databaseAndSchema数据
  databaseAndSchema: MetaSchemaVO;
  // 当前工作区所需的参数
  curWorkspaceParams: ICurWorkspaceParams;
  // 双击树node节点
  doubleClickTreeNodeData: ITreeNode | undefined;
}

export interface IWorkspaceModelType {
  namespace: 'workspace';
  state: IState;
  reducers: {
    setDatabaseAndSchema: Reducer<IState['databaseAndSchema']>;
    setCurWorkspaceParams: Reducer<IState['curWorkspaceParams']>;
    setDoubleClickTreeNodeData: Reducer<any>; //TS TODO:
  };
  effects: {
    fetchDatabaseAndSchema: Effect;
  };
}

const WorkspaceModel: IWorkspaceModelType = {
  namespace: 'workspace',

  state: {
    databaseAndSchema: {},
    curWorkspaceParams: getCurrentWorkspaceDatabase(),
    doubleClickTreeNodeData: undefined,
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
      setCurrentWorkspaceDatabase(payload);
      return {
        ...state,
        curWorkspaceParams: payload,
      };
    },

    setDoubleClickTreeNodeData(state, { payload }) {
      return {
        ...state,
        doubleClickTreeNodeData: payload,
      };
    },
  },

  effects: {
    *fetchDatabaseAndSchema({ payload }, action) {
      const { put } = action;
      // ts-ignore
      const res = yield sqlService.getDatabaseSchemaList(payload);
      yield put({
        type: 'setDatabaseAndSchema',
        payload: res,
      });
    },
  },
};

export default WorkspaceModel;
