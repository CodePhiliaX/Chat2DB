import React, { useState, createContext } from 'react';
import { IOperationData } from '@/components/OperationTableModal';
import { ITreeNode } from '@/types';
import { DatabaseTypeCode } from '@/utils/constants';
import { IEditDataSourceData } from '@/components/CreateConnection';

export type ICreateConsoleDialog =
  | false
  | {
    dataSourceId: number;
    dataSourceName: string;
    databaseName: string;
    schemaName: string;
    databaseType: DatabaseTypeCode;
  };

export type IOperationDataDialog = false | IOperationData;

export interface IModel {
  createConsoleDialog: ICreateConsoleDialog;
  operationData: IOperationDataDialog;
  needRefreshNodeTree: any;
  dblclickNodeData: ITreeNode | null;
  aiImportSql: string;
  showSearchResult: boolean;
  editDataSourceData: IEditDataSourceData | false;
  refreshTreeNum: number;

}

export interface IContext {
  model: IModel;
  setModel: (value: IModel) => void
  setCreateConsoleDialog: (value: ICreateConsoleDialog) => void;
  setOperationDataDialog: (value: IOperationDataDialog) => void;
  setNeedRefreshNodeTree: (value: any) => void;
  setDblclickNodeData: (value: ITreeNode | null) => void;
  setAiImportSql: (value: string) => void;
  setShowSearchResult: (value: boolean) => void;
  setEditDataSourceData: (value: IEditDataSourceData | false) => void;
  setRefreshTreeNum: (value: number) => void;
}

const initDatabaseValue: IModel = {
  createConsoleDialog: false,
  operationData: false,
  needRefreshNodeTree: {},
  dblclickNodeData: null,
  aiImportSql: '',
  // showSearchResult: localStorage.getItem('showSearchResultBox') === 'true',
  showSearchResult: false,
  editDataSourceData: false,
  refreshTreeNum: 0
};

export const DatabaseContext = createContext<IContext>({} as any);

export default function DatabaseContextProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  const [model, setStateModel] = useState<IModel>(initDatabaseValue);

  const setCreateConsoleDialog = (
    createConsoleDialog: ICreateConsoleDialog,
  ) => {
    setStateModel({
      ...model,
      createConsoleDialog,
    });
  };

  const setOperationDataDialog = (operationData: IOperationDataDialog) => {
    setStateModel({
      ...model,
      operationData,
    });
  };

  const setDblclickNodeData = (dblclickNodeData: ITreeNode | null) => {
    setStateModel({
      ...model,
      dblclickNodeData,
    });
  };

  const setNeedRefreshNodeTree = (needRefreshNodeTree: any) => {
    setStateModel({
      ...model,
      needRefreshNodeTree,
    });
  };

  const setAiImportSql = (aiImportSql: any) => {
    setStateModel({
      ...model,
      aiImportSql,
    });
  };

  const setEditDataSourceData = (value: IEditDataSourceData | false) => {
    setStateModel({
      ...model,
      editDataSourceData: value,
    });
  };

  const setShowSearchResult = (showSearchResult: boolean) => {
    setStateModel({
      ...model,
      showSearchResult,
    });
    localStorage.setItem('showSearchResultBox', showSearchResult.toString())
  };

  const setRefreshTreeNum = (refreshTreeNum: number) => {
    setStateModel({
      ...model,
      refreshTreeNum,
    });
  };
  const setModel = (model: IModel) => {
    setStateModel({
      ...model,
    });
  }

  return (
    <DatabaseContext.Provider
      value={{
        model,
        setModel,
        setCreateConsoleDialog,
        setOperationDataDialog,
        setNeedRefreshNodeTree,
        setDblclickNodeData,
        setAiImportSql,
        setShowSearchResult,
        setEditDataSourceData,
        setRefreshTreeNum
      }}
    >
      {children}
    </DatabaseContext.Provider>
  );
}
