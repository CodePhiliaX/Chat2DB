import { Effect, Reducer } from 'umi';
export interface IState {
  curPage: string;
}

export interface IMainPageType {
  namespace: 'mainPage',
  state: IState,
  reducers: {
    updateCurPage: Reducer<IState>;
  };
}

const MainPageModel: IMainPageType = {
  namespace: 'mainPage',

  state: {
    curPage: '',
  },

  reducers: {
    updateCurPage(state, { payload }) {
      return { ...state, curPage: payload };
    },
  },
};

export default MainPageModel
