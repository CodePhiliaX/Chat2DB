import {useCommonStore} from './index'
export interface ICopyFocusedContent {
  focusedContent: any[][]| any[] | string | null;
}

export const initCopyFocusedContent = {
  focusedContent: null,
}

export const setFocusedContent: (content: any[][] | any[] | string | null) => void = (focusedContent) => {
  return useCommonStore.setState({focusedContent})
}
