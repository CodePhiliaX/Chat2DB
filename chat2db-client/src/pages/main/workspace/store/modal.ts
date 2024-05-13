import { useWorkspaceStore } from './index';
import { DatabaseTypeCode } from '@/constants';
import { CreateType } from '@/components/CreateDatabase';
export interface IModalStore {
  openCreateDatabaseModal: ((params: {
    type: CreateType;
    relyOnParams: {
      databaseType: DatabaseTypeCode;
      dataSourceId: number;
      databaseName?: string;
    };
    executedCallback?: (status: true) => void;
  }) => void) | null;
}

export const initModalStore: IModalStore = {
  openCreateDatabaseModal: null,
};

export const setOpenCreateDatabaseModal = (fn: any) => {
  useWorkspaceStore.setState({ openCreateDatabaseModal: fn });
};
