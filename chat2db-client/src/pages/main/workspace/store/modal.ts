import { useWorkspaceStore } from './index';
import { DatabaseTypeCode } from '@/constants';
import { CreateType } from '@/components/CreateDatabase';
import { IImportDataModalParams } from '@/components/ImportDataModal';
import { IExportDataModalParams } from '@/components/ExportDataModal';
import { IExportSchemaDocModalParams } from '@/components/ExportSchemaDocModal';
import { IDataGenerationModalParams } from '@/components/DataGenerationModal';
import { IExecuteSqlStatementModalParams } from '@/components/ExecuteSqlStatementModal';

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
  openImportDataModal: ((params: IImportDataModalParams) => void) | null;
  openExportDataModal: ((params: IExportDataModalParams) => void) | null;
  openExportSchemaDocModal: ((params: IExportSchemaDocModalParams) => void) | null;
  openDataGenerationModal: ((params: IDataGenerationModalParams) => void) | null;
  openExecuteSqlStatementModal: ((params: IExecuteSqlStatementModalParams) => void) | null;
}

export const initModalStore: IModalStore = {
  openCreateDatabaseModal: null,
  openImportDataModal: null,
  openExportDataModal: null,
  openExportSchemaDocModal: null,
  openDataGenerationModal: null,
  openExecuteSqlStatementModal: null,
};

export const setOpenCreateDatabaseModal = (fn: any) => {
  useWorkspaceStore.setState({ openCreateDatabaseModal: fn });
};

export const setOpenImportDataModal = (fn: any) => {
  useWorkspaceStore.setState({ openImportDataModal: fn });
};

export const setOpenExportDataModal = (fn: any) => {
  useWorkspaceStore.setState({ openExportDataModal: fn });
};

export const setOpenExportSchemaDocModal = (fn: any) => {
  useWorkspaceStore.setState({ openExportSchemaDocModal: fn });
};

export const setOpenDataGenerationModal = (fn: any) => {
  useWorkspaceStore.setState({ openDataGenerationModal: fn });
};

export const setOpenExecuteSqlStatementModal = (fn: any) => {
  useWorkspaceStore.setState({ openExecuteSqlStatementModal: fn });
};
