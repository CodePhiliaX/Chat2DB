// 置顶表格
import mysqlService from '@/service/sql';
export const handelPinTable = ({ treeNodeData,loadData }) => {
  const api = treeNodeData.pinned ? 'deleteTablePin' : 'addTablePin';
  mysqlService[api]({
    dataSourceId: treeNodeData.extraParams.dataSourceId,
    databaseName: treeNodeData.extraParams.databaseName,
    schemaName: treeNodeData.extraParams.schemaName,
    tableName: treeNodeData.name,
  }).then(()=>{
    loadData({
      refresh: true,
    })
  })
};
