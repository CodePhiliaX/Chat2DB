import { OperationColumn, WorkspaceTabType } from '@/constants';
import sqlService from '@/service/sql';

export const openView = (props:{
  addWorkspaceTab: any;
  treeNodeData: any;
}) => {
  const {addWorkspaceTab,treeNodeData} = props;
  addWorkspaceTab({
   id: `${OperationColumn.OpenView}-${treeNodeData.uuid}` ,
   title: treeNodeData.name,
   type: WorkspaceTabType.VIEW,
   uniqueData: {
     dataSourceId: treeNodeData.extraParams!.dataSourceId!,
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
   },
  });
}

export const openFunction = (props:{
  addWorkspaceTab: any;
  treeNodeData: any;
}) => {
  const {addWorkspaceTab,treeNodeData} = props;
  addWorkspaceTab({
   id: `${OperationColumn.OpenFunction}-${treeNodeData.uuid}` ,
   title: treeNodeData.name,
   type: WorkspaceTabType.FUNCTION,
   uniqueData: {
     dataSourceId: treeNodeData.extraParams!.dataSourceId!,
     databaseType: treeNodeData.extraParams!.databaseType!,
     databaseName: treeNodeData.extraParams?.databaseName,
     schemaName: treeNodeData.extraParams?.schemaName,
     loadSQL: ()=>{
       return new Promise((resolve) => {
         sqlService
         .getFunctionDetail({
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
   },
  });
}

export const openProcedure = (props:{
  addWorkspaceTab: any;
  treeNodeData: any;
}) => {
  const {addWorkspaceTab,treeNodeData} = props;
  addWorkspaceTab({
   id: `${OperationColumn.OpenProcedure}-${treeNodeData.uuid}` ,
   title: treeNodeData.name,
   type: WorkspaceTabType.PROCEDURE,
   uniqueData: {
     dataSourceId: treeNodeData.extraParams!.dataSourceId!,
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
   },
  });
}

export const openTrigger = (props:{
  addWorkspaceTab: any;
  treeNodeData: any;
}) => {
  const {addWorkspaceTab,treeNodeData} = props;
  addWorkspaceTab({
   id: `${OperationColumn.OpenTrigger}-${treeNodeData.uuid}` ,
   title: treeNodeData.name,
   type: WorkspaceTabType.TRIGGER,
   uniqueData: {
     dataSourceId: treeNodeData.extraParams!.dataSourceId!,
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
   },
  });
}





