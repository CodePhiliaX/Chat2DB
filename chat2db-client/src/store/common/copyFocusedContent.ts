export interface ICopyFocusedContent {
  focusedContent: any[][]| any[] | string | null;
  setFocusedContent: (content: any[][] | any[] | string | null) => void;
}

export const copyFocusedContent = (set): ICopyFocusedContent => ({
  focusedContent: null,
  setFocusedContent: (focusedContent) => set((state) => {
    return {
      ...state,
      focusedContent
    }
  })
});
