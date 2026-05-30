import { WorkspaceTabType } from '@/constants';
import sqlService from '@/service/sql';
import {createConsole} from '@/pages/main/workspace/store/console'

export const openView = (props:{
  addWorkspaceTab: any;
  treeNodeData: any;
}) => {
  const { treeNodeData } = props;
  createConsole({
    name: treeNodeData.name,
    operationType: WorkspaceTabType.VIEW,
    dataSourceId: treeNodeData.extraParams!.dataSourceId!,
    dataSourceName: treeNodeData.extraParams!.dataSourceName!,
    databaseType: treeNodeData.extraParams!.databaseType!,
    databaseName: treeNodeData.extraParams?.databaseName,
    schemaName: treeNodeData.extraParams?.schemaName,
    loadSQL: ()=>{
      return new Promise((resolve, reject) => {
        sqlService
        .getViewDetail({
          dataSourceId: treeNodeData.extraParams!.dataSourceId!,
          databaseType: treeNodeData.extraParams!.databaseType!,
          databaseName: treeNodeData.extraParams!.databaseName!,
          schemaName: treeNodeData.extraParams?.schemaName,
          tableName: treeNodeData.name
        } as any)
        .then((res) => {
          if (res && res.ddl) {
            resolve(res.ddl);
          } else {
            console.warn('[openView] ddl is empty, response:', res);
            resolve('-- View DDL not available or empty');
          }
        })
        .catch((err) => {
          console.error('[openView] Failed to get view detail:', err);
          reject(err);
        });
      });
    }
  })
}

export const openFunction = (props:{
  addWorkspaceTab: any;
  treeNodeData: any;
}) => {
  const { treeNodeData } = props;
  createConsole({
    name: treeNodeData.name,
    operationType: WorkspaceTabType.FUNCTION,
    dataSourceId: treeNodeData.extraParams!.dataSourceId!,
    dataSourceName: treeNodeData.extraParams!.dataSourceName!,
    databaseType: treeNodeData.extraParams!.databaseType!,
    databaseName: treeNodeData.extraParams?.databaseName,
    schemaName: treeNodeData.extraParams?.schemaName,
    loadSQL: ()=>{
      return new Promise((resolve, reject) => {
        sqlService
        .getFunctionDetail({
          dataSourceId: treeNodeData.extraParams!.dataSourceId!,
          databaseType: treeNodeData.extraParams!.databaseType!,
          databaseName: treeNodeData.extraParams!.databaseName!,
          schemaName: treeNodeData.extraParams?.schemaName,
          functionName: treeNodeData.name
        } as any)
        .then((res) => {
          if (res && res.functionBody) {
            resolve(res.functionBody);
          } else {
            console.warn('[openFunction] functionBody is empty, response:', res);
            resolve('-- Function body not available or empty');
          }
        })
        .catch((err) => {
          console.error('[openFunction] Failed to get function detail:', err);
          reject(err);
        });
      });
    }
  })
}

export const openProcedure = (props:{
  addWorkspaceTab: any;
  treeNodeData: any;
}) => {
  const { treeNodeData } = props;
  createConsole({
    name: treeNodeData.name,
    operationType: WorkspaceTabType.PROCEDURE,
    dataSourceId: treeNodeData.extraParams!.dataSourceId!,
    dataSourceName: treeNodeData.extraParams!.dataSourceName!,
    databaseType: treeNodeData.extraParams!.databaseType!,
    databaseName: treeNodeData.extraParams?.databaseName,
    schemaName: treeNodeData.extraParams?.schemaName,
    loadSQL: ()=>{
      return new Promise((resolve, reject) => {
        sqlService
        .getProcedureDetail({
          dataSourceId: treeNodeData.extraParams!.dataSourceId!,
          databaseType: treeNodeData.extraParams!.databaseType!,
          databaseName: treeNodeData.extraParams!.databaseName!,
          schemaName: treeNodeData.extraParams?.schemaName,
          procedureName: treeNodeData.name
        } as any)
        .then((res) => {
          if (res && res.procedureBody) {
            resolve(res.procedureBody);
          } else {
            console.warn('[openProcedure] procedureBody is empty, response:', res);
            resolve('-- Procedure body not available or empty');
          }
        })
        .catch((err) => {
          console.error('[openProcedure] Failed to get procedure detail:', err);
          reject(err);
        });
      });
    }
  })
}

export const openTrigger = (props:{
  addWorkspaceTab: any;
  treeNodeData: any;
}) => {
  const {treeNodeData } = props;
  createConsole({
    name: treeNodeData.name,
    operationType: WorkspaceTabType.TRIGGER,
    dataSourceId: treeNodeData.extraParams!.dataSourceId!,
    dataSourceName: treeNodeData.extraParams!.dataSourceName!,
    databaseType: treeNodeData.extraParams!.databaseType!,
    databaseName: treeNodeData.extraParams?.databaseName,
    schemaName: treeNodeData.extraParams?.schemaName,
    loadSQL: ()=>{
      return new Promise((resolve, reject) => {
        sqlService
        .getTriggerDetail({
          dataSourceId: treeNodeData.extraParams!.dataSourceId!,
          databaseType: treeNodeData.extraParams!.databaseType!,
          databaseName: treeNodeData.extraParams!.databaseName!,
          schemaName: treeNodeData.extraParams?.schemaName,
          triggerName: treeNodeData.name
        } as any)
        .then((res) => {
          if (res && res.triggerBody) {
            resolve(res.triggerBody);
          } else {
            console.warn('[openTrigger] triggerBody is empty, response:', res);
            resolve('-- Trigger body not available or empty');
          }
        })
        .catch((err) => {
          console.error('[openTrigger] Failed to get trigger detail:', err);
          reject(err);
        });
      });
    }
  })
}





