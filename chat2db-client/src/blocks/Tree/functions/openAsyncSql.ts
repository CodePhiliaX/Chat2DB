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
      return new Promise((resolve) => {
        sqlService
        .getViewDetail({
          dataSourceId: treeNodeData.extraParams!.dataSourceId!,
          databaseType: treeNodeData.extraParams!.databaseType!,
          databaseName: treeNodeData.extraParams!.databaseName!,
          schemaName: treeNodeData.extraParams?.schemaName,
          tableName: treeNodeData.name
        } as any)
        .then((res) => {
          // 更新ddl
          resolve(res.ddl);
        });
      });
    }
  })
}

export const openFunction = (props:{
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
      return new Promise((resolve) => {
        sqlService
        .getFunctionDetail({
          name:treeNodeData.name,
          dataSourceId: treeNodeData.extraParams!.dataSourceId!,
          databaseType: treeNodeData.extraParams!.databaseType!,
          databaseName: treeNodeData.extraParams!.databaseName!,
          schemaName: treeNodeData.extraParams?.schemaName,
          functionName: treeNodeData.name
        } as any)
        .then((res) => {
          // 更新ddl
          resolve(res.functionBody);
        });
      });
    }
  })
}

export const openProcedure = (props:{
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
      return new Promise((resolve) => {
        sqlService
        .getProcedureDetail({
          dataSourceId: treeNodeData.extraParams!.dataSourceId!,
          databaseType: treeNodeData.extraParams!.databaseType!,
          databaseName: treeNodeData.extraParams!.databaseName!,
          schemaName: treeNodeData.extraParams?.schemaName,
          procedureName: treeNodeData.name
        } as any)
        .then((res) => {
          // 更新ddl
          resolve(res.procedureBody);
        });
      });
    }
  })
}

export const openTrigger = (props:{
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
      return new Promise((resolve) => {
        sqlService
        .getTriggerDetail({
          dataSourceId: treeNodeData.extraParams!.dataSourceId!,
          databaseType: treeNodeData.extraParams!.databaseType!,
          databaseName: treeNodeData.extraParams!.databaseName!,
          schemaName: treeNodeData.extraParams?.schemaName,
          triggerName: treeNodeData.name
        } as any)
        .then((res) => {
          // 更新ddl
          resolve(res.triggerBody);
        });
      });
    }
  })
}

export const openSequence = (props:{
  treeNodeData: any;
}) => {
  const { treeNodeData } = props;
  createConsole({
    name: treeNodeData.name,
    operationType: WorkspaceTabType.SEQUENCE,
    dataSourceId: treeNodeData.extraParams!.dataSourceId!,
    dataSourceName: treeNodeData.extraParams!.dataSourceName!,
    databaseType: treeNodeData.extraParams!.databaseType!,
    databaseName: treeNodeData.extraParams?.databaseName,
    schemaName: treeNodeData.extraParams?.schemaName,
    loadSQL: ()=>{
      return new Promise((resolve) => {
        sqlService
        .exportCreateSequenceSql({
          dataSourceId: treeNodeData.extraParams!.dataSourceId!,
          databaseType: treeNodeData.extraParams!.databaseType!,
          databaseName: treeNodeData.extraParams!.databaseName!,
          schemaName: treeNodeData.extraParams?.schemaName,
          name: treeNodeData.name
        } as any)
        .then((res) => {
          // 更新ddl
          resolve(res);
        });
      });
    }
  })
}



