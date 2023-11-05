import { create, UseBoundStore, StoreApi } from 'zustand';
import { devtools } from 'zustand/middleware';

import { copyFocusedContent, ICopyFocusedContent } from './copyFocusedContent';

export type IStore = ICopyFocusedContent;

export const useCommonStore: UseBoundStore<StoreApi<IStore>> = create(
  devtools(
    (set) => ({
      ...copyFocusedContent(set),
    }),
  ),
);
