import sqlService from '@/service/sql';

interface ISchema {
  databaseName: string;
  name: string;
}
interface IDatabase {
  name: string;
  schema: ISchema[];
}

const DatabaseModel = {
  namespace: 'database',
  state: {
    databaseAndSchemaList: [],
  },

  reducers: {
    // 设置 database schema 数据
    setDatabaseAndSchemaList(state, { payload }) {
      return {
        ...state,
        databaseAndSchemaList: payload,
      };
    },
  },

  effects: {
    *fetchDatabaseAndSchemaList(p, { call, put }) {
      console.log('fetchDatabaseAndSchemaList start', p);
      const res = (yield sqlService.getDatabaseSchemaList({ dataSourceId: 2 })) as {
        data: { database: IDatabase[]; schema: ISchema[] };
      };
      console.log('fetchDatabaseAndSchemaList end', res);
      yield put({
        type: 'setDatabaseAndSchemaList',
        payload: res,
      });
    },
  },
};

export default DatabaseModel;
