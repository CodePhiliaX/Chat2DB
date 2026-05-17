import { RefObject } from 'react';
import { ISearchResultRef } from '@/components/SearchResult';

const searchResultMap = new Map<number, RefObject<ISearchResultRef>>();

export const registerSearchResult = (consoleId: number, ref: RefObject<ISearchResultRef>) => {
  console.log('[searchResultRegistry] Registering searchResult for consoleId:', consoleId);
  searchResultMap.set(consoleId, ref);
};

export const unregisterSearchResult = (consoleId: number) => {
  console.log('[searchResultRegistry] Unregistering searchResult for consoleId:', consoleId);
  searchResultMap.delete(consoleId);
};

export const getSearchResult = (consoleId: number) => {
  return searchResultMap.get(consoleId);
};
