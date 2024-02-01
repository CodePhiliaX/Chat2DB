import { DatabaseTypeCode } from '@/constants';
import { CreateType } from '@/components/CreateDatabase';

export interface ModalState {
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

export const initModalState = {
  openCreateDatabaseModal: null,
};
