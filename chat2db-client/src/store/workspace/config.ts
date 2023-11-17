import {useWorkspaceStore} from './index'
export interface IConfigStore {
  layout: {
    panelLeft: boolean;
    panelLeftWidth: number;
    panelRight: boolean;
  };
}

export const initConfigStore: IConfigStore = {
  layout: {
    panelLeft: true,
    panelRight: false,
    panelLeftWidth: 220,
  },
}

export const togglePanelRight = () => {
  return useWorkspaceStore.setState((state) => ({
    layout: {
      ...state.layout,
      panelRight: !state.layout.panelRight,
    },
  }))
}

export const togglePanelLeft = () => {
  return useWorkspaceStore.setState((state) => ({
    layout: {
      ...state.layout,
      panelLeft: !state.layout.panelLeft,
    },
  }))
}
