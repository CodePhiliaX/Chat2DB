export interface MainState {
  curPage: string;
}

//全局配置信息
const mainModel = {
  namespace: 'mainPage',

  state: {
    curPage: 'connections',
  },

  reducers: {
    updateCurPage(state: MainState, { payload }: { payload: MainState['curPage'] }) {
      return { ...state, curPage: payload };
    },
  },
};

export default mainModel
