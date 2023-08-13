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
  curConsoleId: number | null;
  openConsoleList: IConsole[];
  curTableList: ITreeNode[];
  curViewList: ITreeNode[];
}

export interface IWorkspaceModelType {
  namespace: 'workspace';
  state: IWorkspaceModelState;
  reducers: {
    // TS TODO:
    setDatabaseAndSchema: Reducer<IWorkspaceModelState>;
    setCurWorkspaceParams: Reducer<IWorkspaceModelState>;
    setDoubleClickTreeNodeData: Reducer<IWorkspaceModelState>;
    setConsoleList: Reducer<IWorkspaceModelState>;
    setOpenConsoleList: Reducer<IWorkspaceModelState>;
    setCurConsoleId: Reducer<IWorkspaceModelState>;
    setCurTableList: Reducer<IWorkspaceModelState>;
    setCurViewList: Reducer<IWorkspaceModelState>;
  };
  effects: {
    fetchDatabaseAndSchema: Effect;
    fetchDatabaseAndSchemaLoading: Effect;
    fetchGetSavedConsole: Effect;
    fetchGetCurTableList: Effect;
    fetchGetSavedConsoleLoading: Effect;
    fetchGetCurViewList: Effect;
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
    curViewList: [],
    curConsoleId: null
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

    // 工作台页面打开的console列表
    setOpenConsoleList(state, { payload }) {
      return {
        ...state,
        openConsoleList: payload,
      };
    },

    // 当前聚焦的console
    setCurConsoleId(state, { payload }) {
      return {
        ...state,
        curConsoleId: payload
      }
    },

    setCurTableList(state, { payload }) {
      return {
        ...state,
        curTableList: payload,
      };
    },

    // 视图列表
    setCurViewList(state, { payload }) {
      return {
        ...state,
        curViewList: payload,
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
    *fetchGetCurViewList({ payload, callback }, { put, call }) {
      try {
        const res = (yield treeConfig[TreeNodeType.VIEWS].getChildren!({
          pageNo: 1,
          pageSize: 999,
          ...payload,
        })) as ITreeNode[];
        // 异步操作完成后调用回调函数
        if (callback && typeof callback === 'function') {
          callback(res);
        }
        yield put({
          type: 'setCurViewList',
          payload: res,
        });
      }
      catch {

      }
    },
  },
};

export default WorkspaceModel;
