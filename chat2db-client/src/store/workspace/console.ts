export interface IConsoleStore {
  activeTab: {
    activeConsole: {
      id: string | number | null;
    };
    activeSearchResult: {
      id: string | number | null;
    };
  };
  setActiveConsole: (props: IActiveTabData) => void;
  setActiveSearchResult: (props: IActiveTabData) => void;
}

type IActiveTabData = {
  id: string | number;
} | null

export const consoleStore = (set): IConsoleStore => ({
  activeTab: {
    activeConsole: {
      id: null,
    },
    activeSearchResult: {
      id: null,
    },
  },
  setActiveConsole: (activeConsole: IActiveTabData) => {
    return set((state) => ({
      activeTab: {
        ...state.activeTab,
        activeConsole,
      },
    }));
  },
  setActiveSearchResult: (activeSearchResult: IActiveTabData) => {
    return set((state) => ({
      activeTab: {
        ...state.activeTab,
        activeSearchResult,
      },
    }));
  },
});
