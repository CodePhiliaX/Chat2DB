import { getCurrentWorkspaceDatabase, setCurrentWorkspaceDatabase } from '@/utils/localStorage';
import sqlService, { MetaSchemaVO } from '@/service/sql';
import historyService from '@/service/history'
import { DatabaseTypeCode, ConsoleStatus, TreeNodeType } from '@/constants';
import { Effect, Reducer } from 'umi';
import { ITreeNode, IConsole } from '@/typings';
import { treeConfig } from '@/pages/main/workspace/components/Tree/treeConfig';

export type ICurWorkspaceParams = {
  dataSourceId: number;
  databaseSourceName: string;
  databaseType: DatabaseTypeCode;
  databaseName?: string;
  schemaName?: string;
};

export interface IState {
  // 当前连接下的及联databaseAndSchema数据
  databaseAndSchema: MetaSchemaVO | undefined;
  // 当前工作区所需的参数
  curWorkspaceParams: ICurWorkspaceParams;
  // 双击树node节点
  doubleClickTreeNodeData: ITreeNode | undefined;
  consoleList: IConsole[];
  curTableList: ITreeNode[];
}

export interface IWorkspaceModelType {
  namespace: 'workspace';
  state: IState;
  reducers: {
    setDatabaseAndSchema: Reducer<IState['databaseAndSchema']>;
    setCurWorkspaceParams: Reducer<IState['curWorkspaceParams']>;
    setDoubleClickTreeNodeData: Reducer<any>; //TS TODO:
    setConsoleList: Reducer<IState['consoleList']>;
    setCurTableList: Reducer<IState['curTableList']>;
  };
  effects: {
    fetchDatabaseAndSchema: Effect;
    fetchGetSavedConsole: Effect;
    fetchGetCurTableList: Effect;
  };
}

const WorkspaceModel: IWorkspaceModelType = {
  namespace: 'workspace',

  state: {
    databaseAndSchema: undefined,
    curWorkspaceParams: getCurrentWorkspaceDatabase(),
    doubleClickTreeNodeData: undefined,
    consoleList: [],
    curTableList: []
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

    setConsoleList(state, { payload }) {
      return {
        ...state,
        consoleList: payload,
      };
    },
    setCurTableList(state, { payload }) {
      return {
        ...state,
        curTableList: payload,
      };
    },
  },

  effects: {
    *fetchDatabaseAndSchema({ payload }, { put }) {
      const res = yield sqlService.getDatabaseSchemaList(payload);
      yield put({
        type: 'setDatabaseAndSchema',
        payload: res,
      });
    },
    *fetchGetSavedConsole({ payload }, { put }) {
      const res = yield historyService.getSavedConsoleList({
        pageNo: 1,
        pageSize: 999,
        status: ConsoleStatus.RELEASE
      });
      yield put({
        type: 'setConsoleList',
        payload: res.data,
      });
    },
    *fetchGetCurTableList({ payload }, { put }) {
      const res = yield treeConfig[TreeNodeType.TABLES].getChildren!({
        pageNo: 1,
        pageSize: 999,
        ...payload
      });
      yield put({
        type: 'setCurTableList',
        payload: res,
      });
    },
  },
};

export default WorkspaceModel;
