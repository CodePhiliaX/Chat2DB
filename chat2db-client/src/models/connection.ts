import { IConnectionDetails } from '@/typings/connection';
import { Effect, Reducer } from 'umi';
import connectionService from '@/service/connection';
import { IPageResponse } from '@/typings/common';

/**
 * 数据源相关 - 链接池、数据库、schema、表
 */
export interface ConnectionModelState {
  curConnection?: IConnectionDetails;
  connectionList: IConnectionDetails[];
}

export interface ConnectionModelType {
  namespace: 'connection';
  state: ConnectionModelState;
  reducers: {
    // 设置连接池列表
    setConnectionList: Reducer<ConnectionModelState>;
    setCurConnection: Reducer<ConnectionModelState>;
  };
  effects: {
    // setConnectionList: Effect;
  };
}

// const ConnectionModel:ConnectionModelType = {
const ConnectionModel = {
  namespace: 'connection',
  state: {
    curConnection: undefined,
    connectionList: [],
  },
  reducers: {
    // 设置连接池列表
    setConnectionList(state: ConnectionModelState, { payload }: { payload: ConnectionModelState['connectionList'] }) {
      return {
        ...state,
        connectionList: payload,
      };
    },

    // 设置当前选着的Connection
    setCurConnection(state: ConnectionModelState, { payload }: { payload: ConnectionModelState['curConnection'] }) {
      return { ...state, curConnection: payload };
    },

  },

  effects: {
    *fetchConnectionList(_, { call, put }) {
      const res = (yield connectionService.getList({ pageNo: 1, pageSize: 999 })) as IPageResponse<IConnectionDetails>;
      console.log('fetchConnectionList==>', res.data);
      yield put({
        type: 'setConnectionList',
        payload: res.data,
      });
    },

  },
};

export default ConnectionModel;
