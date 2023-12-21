import { UseBoundStoreWithEqualityFn, createWithEqualityFn } from 'zustand/traditional';
import { devtools } from 'zustand/middleware';
import { shallow } from 'zustand/shallow';
import { StoreApi } from 'zustand';

import { initCopyFocusedContent, ICopyFocusedContent } from './copyFocusedContent';
import { initComponentsContent, IComponentsContent } from './components';

export type IStore = ICopyFocusedContent & IComponentsContent;

export const useCommonStore: UseBoundStoreWithEqualityFn<StoreApi<IStore>> = createWithEqualityFn(
  devtools(
    () => ({
      ...initCopyFocusedContent,
      ...initComponentsContent,
    }),
  ),
  shallow
);
