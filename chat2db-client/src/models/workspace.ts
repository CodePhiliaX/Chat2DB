import { getCurrentWorkspaceDatabase, setCurrentWorkspaceDatabase } from '@/utils/localStorage';
import sqlService, { MetaSchemaVO } from '@/service/sql';
import historyService from '@/service/history';
import { DatabaseTypeCode, ConsoleStatus, TreeNodeType } from '@/constants';
import { Effect, Reducer } from 'umi';
import { ITreeNode, IConsole, IPageResponse } from '@/typings';
import { treeConfig } from '@/pages/main/workspace/components/Tree/treeConfig';

export type ICurWorkspaceParams = {
  dataSourceId: number;
  dataSourceName: string;
  databaseType: DatabaseTypeCode;
  databaseName?: string | null; // 这里可以是null 因为有的数据库不需要databaseName 和 schemaName 用null来区分 undefined
  schemaName?: string | null;
};

export interface IWorkspaceModelState {
  // 当前连接下的及联databaseAndSchema数据
  databaseAndSchema: MetaSchemaVO | undefined;
  // 当前工作区所需的参数
  curWorkspaceParams: ICurWorkspaceParams;
  // 双击树node节点
  doubleClickTreeNodeData: ITreeNode | undefined;
  consoleList: IConsole[];
  openConsoleList: IConsole[];
  curTableList: ITreeNode[];
}

export interface IWorkspaceModelType {
  namespace: 'workspace';
  state: IWorkspaceModelState;
  reducers: {
    setDatabaseAndSchema: Reducer<IWorkspaceModelState['databaseAndSchema']>;
    setCurWorkspaceParams: Reducer<IWorkspaceModelState['curWorkspaceParams']>;
    setDoubleClickTreeNodeData: Reducer<any>; //TS TODO:
    setConsoleList: Reducer<IWorkspaceModelState['consoleList']>;
    setOpenConsoleList: Reducer<IWorkspaceModelState['consoleList']>;
    setCurTableList: Reducer<IWorkspaceModelState['curTableList']>;
  };
  effects: {
    fetchDatabaseAndSchema: Effect;
    fetchDatabaseAndSchemaLoading: Effect;
    fetchGetSavedConsole: Effect;
    fetchGetCurTableList: Effect;
    fetchGetSavedConsoleLoading: Effect;
  };
}

const WorkspaceModel: IWorkspaceModelType = {
  namespace: 'workspace',

  state: {
    databaseAndSchema: undefined,
    curWorkspaceParams: getCurrentWorkspaceDatabase(),
    doubleClickTreeNodeData: undefined,
    consoleList: [],
    openConsoleList: [],
    curTableList: [],
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

    setOpenConsoleList(state, { payload }) {
      return {
        ...state,
        openConsoleList: payload,
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
    *fetchDatabaseAndSchema({ payload, callback }, { put }) {
      try {
        const res = (yield sqlService.getDatabaseSchemaList(payload)) as MetaSchemaVO;
        yield put({
          type: 'setDatabaseAndSchema',
          payload: res,
        });
        if (callback && typeof callback === 'function') {
          callback(res);
        }
      }
      catch {

      }
    },
    *fetchDatabaseAndSchemaLoading({ payload }, { put }) {
      try {
        const res = (yield sqlService.getDatabaseSchemaList(payload)) as MetaSchemaVO;
        yield put({
          type: 'setDatabaseAndSchema',
          payload: res,
        });
      }
      catch {

      }
    },
    *fetchGetSavedConsole({ payload, callback }, { put }) {
      try {
        const res = (yield historyService.getSavedConsoleList({
          pageNo: 1,
          pageSize: 999,
          ...payload
        })) as IPageResponse<IConsole>;
        if (callback && typeof callback === 'function') {
          callback(res);
        }
      }
      catch {
      }
    },
    *fetchGetSavedConsoleLoading({ payload, callback }, { put }) {
      try {
        const res = (yield historyService.getSavedConsoleList({
          pageNo: 1,
          pageSize: 999,
          ...payload
        })) as IPageResponse<IConsole>;
        if (callback && typeof callback === 'function') {
          callback(res);
        }
      }
      catch {
      }
    },
    *fetchGetCurTableList({ payload, callback }, { put, call }) {
      try {
        const res = (yield treeConfig[TreeNodeType.TABLES].getChildren!({
          pageNo: 1,
          pageSize: 999,
          ...payload,
        })) as ITreeNode[];
        // 异步操作完成后调用回调函数
        if (callback && typeof callback === 'function') {
          callback(res);
        }
        yield put({
          type: 'setCurTableList',
          payload: res,
        });
      }
      catch {

      }
    },
  },
};

export default WorkspaceModel;
