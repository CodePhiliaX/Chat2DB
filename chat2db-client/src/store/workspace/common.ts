import { IConnectionDetails } from '@/typings/connection';

export interface ICommonStore {
  currentConnectionDetails: Partial<IConnectionDetails> | null;
  setCurrentConnectionDetails: (connectionDetails: Partial<IConnectionDetails> | null) => void;
}

export const commonStore = (set): ICommonStore => ({
  currentConnectionDetails: null,
  setCurrentConnectionDetails: (connectionDetails: ICommonStore['currentConnectionDetails']) =>
    set({ currentConnectionDetails: connectionDetails }),
});
