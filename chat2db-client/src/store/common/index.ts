import { UseBoundStoreWithEqualityFn, createWithEqualityFn } from 'zustand/traditional';
import { devtools } from 'zustand/middleware';
import { shallow } from 'zustand/shallow';
import { StoreApi } from 'zustand';

import { initCopyFocusedContent, ICopyFocusedContent } from './copyFocusedContent';

export type IStore = ICopyFocusedContent;

export const useCommonStore: UseBoundStoreWithEqualityFn<StoreApi<IStore>> = createWithEqualityFn(
  devtools(
    () => ({
      ...initCopyFocusedContent,
    }),
  ),
  shallow
);
