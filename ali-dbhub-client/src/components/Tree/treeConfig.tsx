import { ITreeNode } from '@/types';
import { TreeNodeType } from '@/utils/constants';
import connectionService from '@/service/connection';
import mysqlServer from '@/service/mysql';

export type ITreeConfig = Partial<{ [key in TreeNodeType]: ITreeConfigItem }>;

export const switchIcon: Partial<{ [key in TreeNodeType]: { icon: string } }> = {
  [TreeNodeType.DATABASE]: {
    icon: '\ue62c'
  },
  [TreeNodeType.SCHEMAS]: {
    icon: '\ue696'
  },
  [TreeNodeType.TABLE]: {
    icon: '\ue63e'
  },
  [TreeNodeType.TABLES]: {
    icon: '\ueac5'
  },
  [TreeNodeType.COLUMNS]: {
    icon: '\ueac5'
  },
  [TreeNodeType.COLUMN]: {
    icon: '\ue611'
  },
  [TreeNodeType.KEYS]: {
    icon: '\ueac5'
  },
  [TreeNodeType.KEY]: {
    icon: '\ue775'
  },
  [TreeNodeType.INDEXES]: {
    icon: '\ueac5'
  },
  [TreeNodeType.INDEX]: {
    icon: '\ue65b'
  },
  // [TreeNodeType.SEARCH]: {
  //   icon: '\uec4c'
  // },
  // [TreeNodeType.LINE]: {
  //   icon: '\ue611'
  // },
  // [TreeNodeType.LINETOTAL]: {
  //   icon: '\ue611'
  // },
  // [TreeNodeType.SAVE]: {
  //   icon: '\ue936'
  // },
  // [TreeNodeType.INDEXESTOTAL]: {
  //   icon: '\ue648'
  // }
}

export enum OperationColumn {
  REFRESH = 'refresh',
  ShiftOut = 'shiftOut',
  CreateTable = 'createTable',
  CreateConsole = 'createConsole',
  DeleteTable = 'deleteTable',
  ExportDDL = 'exportDDL',
  EditSource = 'editSource'
}

export interface ITreeConfigItem {
  icon?: string;
  getChildren?: (data: ITreeNode) => Promise<ITreeNode[]>;
  next?: TreeNodeType;
  operationColumn?: OperationColumn[]
}

export const treeConfig: { [key in TreeNodeType]: ITreeConfigItem } = {
  [TreeNodeType.DATASOURCES]: {
    getChildren: () => {
      return new Promise((r: (value: ITreeNode[]) => void, j) => {
        let p = {
          pageNo: 1,
          pageSize: 999
        }

        connectionService.getList(p).then(res => {
          const data: ITreeNode[] = res.data.map(t => {
            return {
              name: t.alias,
              url: t.EnvType,
              user: t.user,
              key: t.id!.toString(),
              nodeType: TreeNodeType.DATASOURCE,
              dataSourceId: t.id,
              dataSourceName: t.alias,
              dataType: t.type,
            }
          })
          r(data);
        }).catch(error => {
          j()
        })
      })
    },
  },
  [TreeNodeType.DATASOURCE]: {
    getChildren: (parentData: ITreeNode) => {
      console.log(parentData)
      return new Promise((r: (value: ITreeNode[]) => void, j) => {
        let p = {
          id: parentData.dataSourceId!
        }
        connectionService.getDBList(p).then(res => {
          const data: ITreeNode[] = res.map(t => {
            return {
              key: t.name,
              name: t.name,
              nodeType: TreeNodeType.DATABASE,
              dataType: parentData.dataType,
              dataSourceId: +parentData.dataSourceId!,
              dataSourceName: parentData.dataSourceName!,
              databaseName: t.name
            }
          })
          r(data);
        }).catch(error => {
          j()
        })
      })
    },
    operationColumn: [
      OperationColumn.EditSource, OperationColumn.REFRESH, OperationColumn.ShiftOut
    ],
    next: TreeNodeType.DATABASE
  },
  [TreeNodeType.DATABASE]: {
    icon: '\ue62c',
    getChildren: (parentData: ITreeNode) => {
      return new Promise((r: (value: ITreeNode[]) => void, j) => {
        let p = {
          dataSourceId: parentData.dataSourceId!,
          databaseName: parentData.databaseName!
        }
        mysqlServer.getSchemaList(p).then(res => {
          const data: ITreeNode[] = res.map(t => {
            return {
              key: t.name,
              name: t.name,
              nodeType: TreeNodeType.SCHEMAS,
              dataType: parentData.dataType,
              schemaName: t.name,
              dataSourceId: parentData.dataSourceId!,
              dataSourceName: parentData.dataSourceName!,
              databaseName: parentData.databaseName!
            }
          })
          if (data.length) {
            r(data);
          } else {
            let data = [
              {
                key: parentData.name + 'tables',
                name: 'tables',
                nodeType: TreeNodeType.TABLES,
                dataSourceId: parentData.dataSourceId,
                dataSourceName: parentData.dataSourceName!,
                databaseName: parentData.databaseName,
                dataType: parentData.dataType,
                schemaName: parentData.schemaName,
              }
            ]
            r(data);
          }
        }).catch(error => {
          j()
        })
      })
    },
    operationColumn: [
      OperationColumn.CreateConsole, OperationColumn.CreateTable, OperationColumn.REFRESH
    ],
    next: TreeNodeType.SCHEMAS
  },
  [TreeNodeType.SCHEMAS]: {
    icon: '\ue696',
    getChildren: (parentData: ITreeNode) => {
      return new Promise((r: (value: ITreeNode[]) => void, j) => {
        let data = [
          {
            key: parentData.name + 'tables',
            name: 'tables',
            nodeType: TreeNodeType.TABLES,
            dataSourceId: parentData.dataSourceId,
            dataSourceName: parentData.dataSourceName!,
            databaseName: parentData.databaseName,
            dataType: parentData.dataType,
            schemaName: parentData.schemaName,
          }
        ]
        r(data);
      })
    },
    operationColumn: [
      OperationColumn.CreateConsole, OperationColumn.CreateTable, OperationColumn.REFRESH
    ],
  },
  [TreeNodeType.TABLES]: {
    icon: '\ueac5',
    getChildren: (parentData: ITreeNode) => {
      return new Promise((r: (value: ITreeNode[]) => void, j) => {
        let p = {
          dataSourceId: parentData.dataSourceId!,
          databaseName: parentData.databaseName!,
          schemaName: parentData.schemaName,
          pageNo: 1,
          pageSize: 100,
        }

        mysqlServer.getList(p).then(res => {
          const tableList: ITreeNode[] = res.data?.map(item => {
            return {
              name: item.name,
              nodeType: TreeNodeType.TABLE,
              key: item.name,
              dataSourceId: parentData.dataSourceId!,
              dataSourceName: parentData.dataSourceName!,
              databaseName: parentData.databaseName!,
              dataType: parentData.dataType,
              tableName: item.name,
              schemaName: parentData.schemaName,
            }
          })
          r(tableList);
        }).catch(error => {
          j()
        })
      })
    },
    operationColumn: [
      OperationColumn.CreateConsole, OperationColumn.CreateTable, OperationColumn.REFRESH
    ],
  },
  [TreeNodeType.TABLE]: {
    icon: '\ue63e',
    getChildren: (parentData: ITreeNode) => {
      return new Promise((r: (value: ITreeNode[]) => void, j) => {
        const tableList = [
          {
            name: 'columns',
            nodeType: TreeNodeType.COLUMNS,
            key: 'columns',
            tableName: parentData.tableName,
            dataSourceId: parentData.dataSourceId!,
            dataSourceName: parentData.dataSourceName!,
            databaseName: parentData.databaseName!,
            dataType: parentData.dataType,
            schemaName: parentData.schemaName,
          },
          {
            name: 'keys',
            nodeType: TreeNodeType.KEYS,
            key: 'keys',
            tableName: parentData.tableName,
            dataSourceId: parentData.dataSourceId!,
            dataSourceName: parentData.dataSourceName!,
            databaseName: parentData.databaseName!,
            dataType: parentData.dataType,
            schemaName: parentData.schemaName,
          },
          {
            name: 'indexs',
            nodeType: TreeNodeType.INDEXES,
            key: 'indexs',
            tableName: parentData.tableName,
            dataSourceId: parentData.dataSourceId!,
            dataSourceName: parentData.dataSourceName!,
            databaseName: parentData.databaseName!,
            dataType: parentData.dataType,
            schemaName: parentData.schemaName,
          },
        ]

        r(tableList);
      })
    },
    operationColumn: [
      OperationColumn.CreateConsole, OperationColumn.ExportDDL, OperationColumn.DeleteTable
    ],
  },
  [TreeNodeType.COLUMNS]: {
    icon: '\ueac5',
    getChildren: (parentData: ITreeNode) => {
      return new Promise((r: (value: ITreeNode[]) => void, j) => {
        let p = {
          dataSourceId: parentData.dataSourceId!,
          dataSourceName: parentData.dataSourceName!,
          databaseName: parentData.databaseName!,
          tableName: parentData.tableName!,
          dataType: parentData.dataType,
          schemaName: parentData.schemaName,
        }
        console.log(p)

        mysqlServer.getColumnList(p).then(res => {
          const tableList: ITreeNode[] = res?.map(item => {
            return {
              name: item.name,
              nodeType: TreeNodeType.COLUMN,
              key: item.name,
              isLeaf: true,
              columnType: item.columnType,
              dataSourceId: parentData.dataSourceId!,
              dataSourceName: parentData.dataSourceName!,
              databaseName: parentData.databaseName!,
              tableName: parentData.tableName!,
              dataType: parentData.dataType,
              schemaName: parentData.schemaName,
            }
          })
          r(tableList);
        }).catch(error => {
          j()
        })
      })
    }
  },
  [TreeNodeType.COLUMN]: {
    icon: '\ue611'
  },
  [TreeNodeType.KEYS]: {
    icon: '\ueac5',
    getChildren: (parentData: ITreeNode) => {
      return new Promise((r: (value: ITreeNode[]) => void, j) => {
        let p = {
          dataSourceId: parentData.dataSourceId!,
          databaseName: parentData.databaseName!,
          tableName: parentData.tableName!,
          schemaName: parentData.schemaName,
        }

        mysqlServer.getKeyList(p).then(res => {
          console.log(res)
          const tableList: ITreeNode[] = res?.map(item => {
            return {
              name: item.name,
              nodeType: TreeNodeType.KEY,
              key: item.name,
              isLeaf: true,
              dataSourceId: parentData.dataSourceId!,
              dataSourceName: parentData.dataSourceName!,
              databaseName: parentData.databaseName!,
              tableName: parentData.tableName!,
              dataType: parentData.dataType,
              schemaName: parentData.schemaName,
            }
          })
          r(tableList);
        }).catch(error => {
          j()
        })
      })
    }

  },
  [TreeNodeType.KEY]: {
    icon: '\ue775'
  },
  [TreeNodeType.INDEXES]: {
    icon: '\ueac5',
    getChildren: (parentData: ITreeNode) => {
      return new Promise((r: (value: ITreeNode[]) => void, j) => {
        let p = {
          dataSourceId: parentData.dataSourceId!,
          databaseName: parentData.databaseName!,
          tableName: parentData.tableName!,
          schemaName: parentData.schemaName,
        }

        mysqlServer.getIndexList(p).then(res => {
          const tableList: ITreeNode[] = res?.map(item => {
            return {
              name: item.name,
              nodeType: TreeNodeType.INDEX,
              key: item.name,
              isLeaf: true,
              dataSourceId: parentData.dataSourceId!,
              dataSourceName: parentData.dataSourceName!,
              databaseName: parentData.databaseName!,
              tableName: parentData.tableName!,
              dataType: parentData.dataType,
              schemaName: parentData.schemaName,
            }
          })
          r(tableList);
        }).catch(error => {
          j()
        })
      })
    }
  },
  [TreeNodeType.INDEX]: {
    icon: '\ue65b'
  },
  // [TreeNodeType.SEARCH]: {
  //   icon: '\uec4c'
  // },
  // [TreeNodeType.LINE]: {
  //   icon: '\ue611'
  // },
  // [TreeNodeType.LINETOTAL]: {
  //   icon: '\ue611'
  // },
  // [TreeNodeType.SAVE]: {
  //   icon: '\ue936'
  // },
  // [TreeNodeType.INDEXESTOTAL]: {
  //   icon: '\ue648'
  // }
}