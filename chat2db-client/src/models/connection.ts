import { IConnectionDetails } from '@/typings/connection';
import { Effect, Reducer } from 'umi';
import connectionService from '@/service/connection';
import { IPageResponse } from '@/typings/common';
import { getCurConnection } from '@/utils/localStorage';

/**
 * 数据源相关 - 链接池、数据库、schema、表
 */
export interface IConnectionModelState {
  curConnection?: IConnectionDetails;
  connectionList: IConnectionDetails[];
}

export interface IConnectionModelType {
  namespace: 'connection';
  state: IConnectionModelState;
  reducers: {
    // 设置连接列表
    setConnectionList: Reducer<IConnectionModelState>;
    setCurConnection: Reducer<IConnectionModelState>;
  };
  effects: {
    fetchConnectionList: Effect;
  };
}

const ConnectionModel: IConnectionModelType = {
  namespace: 'connection',
  state: {
    curConnection: getCurConnection(),
    connectionList: [],
  },
  reducers: {
    // 设置连接列表
    setConnectionList(state, { payload }) {
      return {
        ...state,
        connectionList: payload,
      };
    },

    // 设置当前选着的Connection
    setCurConnection(state, { payload }) {
      localStorage.setItem('cur-connection', JSON.stringify(payload));
      return { ...state, curConnection: payload };
    },
  },

  effects: {
    *fetchConnectionList({ callback, payload }, { call, put }) {
      try {
        const res = (yield connectionService.getList({ pageNo: 1, pageSize: 999, refresh: payload?.refresh })) as IPageResponse<IConnectionDetails>;
        yield put({
          type: 'setConnectionList',
          payload: res.data,
        });
        if (callback && typeof callback === 'function') {
          callback(res);
        }
      }
      catch {

      }
    },
  },
};

export default ConnectionModel;
