import sqlService,{MetaSchemaVO} from '@/service/sql';
import { Effect, Reducer } from 'umi';

interface IState {
  databaseAndSchema: MetaSchemaVO
}

export interface DatabaseModelType {
  namespace: 'database';
  state: IState;
  reducers: {
    setDatabaseAndSchema: Reducer<IState['databaseAndSchema']>;
  };
  effects: {
    fetchdatabaseAndSchema: Effect;
  };
}

const DatabaseModel:DatabaseModelType = {
  namespace: 'database',
  state: {
    databaseAndSchema: {} ,
  },

  reducers: {
    // 设置 database schema 数据
    setDatabaseAndSchema(state, { payload }) {
      return {
        ...state,
        databaseAndSchema: payload,
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

export default DatabaseModel;
