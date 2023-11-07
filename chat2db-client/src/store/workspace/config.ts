export interface IConfigStore {
  layout: {
    panelLeft: boolean;
    panelLeftWidth: number;
    panelRight: boolean;
  };
  togglePanelLeft: () => void;
  togglePanelRight: () => void;
}


export const configStore = (set):IConfigStore => ({
  layout: {
    panelLeft: true,
    panelRight: false,
    panelLeftWidth: 220,
  },
  togglePanelLeft: () =>
    set((state) => ({
      layout: {
        ...state.layout,
        panelLeft: !state.layout.panelLeft,
      },
    })),
  togglePanelRight: () =>
    set((state) => ({
      layout: {
        ...state.layout,
        panelRight: !state.layout.panelRight,
      },
    })),
})
