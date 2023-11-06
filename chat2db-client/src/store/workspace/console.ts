export interface IConsoleStore {
  activeTab: {
    activeConsole: {
      id: string | null;
    };
    activeSearchResult: {
      id: string | null;
    };
  };
  setActiveConsole: (id: string | null) => void;
  setActiveSearchResult: (id: string | null) => void;
}

export const consoleStore = (set):IConsoleStore => ({
  activeTab: {
    activeConsole: {
      id: null,
    },
    activeSearchResult: {
      id: null,
    },
  },
  setActiveConsole: (id: string | null) => {
    return set((state) => ({
      activeTab: {
        ...state.activeTab,
        activeConsole: {
          id,
        },
      },
    }))
  },
  setActiveSearchResult: (id: string | null) => {
    return set((state) => ({
      activeTab: {
        ...state.activeTab,
        activeSearchResult: {
          id,
        },
      },
    }))
  }
})
