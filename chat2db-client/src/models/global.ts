export interface GlobalState {
  settings: {
    theme: 'light' | 'dark'; // 主题风格
    language: 'zh' | 'en'; // 语言
    // ...
  };
}

//全局配置信息
export default {
  namespace: 'global',
  state: {
    // 全局设置信息
    settings: {
      theme: 'light', // 主题风格
      language: 'zh', // 语言
      // ...
    },
  },

  reducers: {
    // 更新全局设置信息
    updateSettings(
      state: GlobalState,
      { payload }: { payload: GlobalState['settings'] },
    ) {
      return { ...state, settings: payload };
    },
  },
};
