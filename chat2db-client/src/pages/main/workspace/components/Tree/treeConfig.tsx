import { ITreeNode } from '@/typings';
import { TreeNodeType } from '@/constants';
import connectionService from '@/service/connection';
import mysqlServer, { ISchemaParams, IGetListParams, ITableParams } from '@/service/sql';


export type ITreeConfig = Partial<{ [key in TreeNodeType]: ITreeConfigItem }>;

export const switchIcon: Partial<{ [key in TreeNodeType]: { icon: string, unfoldIcon?: string } }> = {
  [TreeNodeType.DATABASE]: {
    icon: '\ue62c',
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
    icon: '\ueabe',
    unfoldIcon: '\ueabf'
  },
  [TreeNodeType.COLUMN]: {
    icon: '\ue611'
  },
  [TreeNodeType.KEYS]: {
    icon: '\ueabe',
    unfoldIcon: '\ueabf'
  },
  [TreeNodeType.KEY]: {
    icon: '\ue775',
  },
  [TreeNodeType.INDEXES]: {
    icon: '\ueabe',
    unfoldIcon: '\ueabf'
  },
  [TreeNodeType.INDEX]: {
    icon: '\ue65b'
  },
}

export enum OperationColumn {
  Refresh = 'refresh',
  ShiftOut = 'shiftOut',
  CreateTable = 'createTable',
  CreateConsole = 'createConsole',
  DeleteTable = 'deleteTable',
  ExportDDL = 'exportDDL',
  EditSource = 'editSource',
  Top = 'top'

}

export interface ITreeConfigItem {
  icon?: string;
  getChildren?: (params: any) => Promise<ITreeNode[]>;
  next?: TreeNodeType;
  operationColumn?: OperationColumn[];
}

export const treeConfig: { [key in TreeNodeType]: ITreeConfigItem } = {
  [TreeNodeType.DATA_SOURCES]: {
    getChildren: () => {
      return new Promise((r: (value: ITreeNode[]) => void, j) => {
        let p = {
          pageNo: 1,
          pageSize: 999
        }
        connectionService.getList(p).then(res => {
          const data: ITreeNode[] = res.data.map(t => {
            return {
              key: t.id!,
              name: t.alias,
              treeNodeType: TreeNodeType.DATA_SOURCE,
              extraParams: {
                databaseType: t.type,
                dataSourceId: t.id,
                dataSourceName: t.name,
              }
            }
          })
          r(data);
        }).catch(error => {
          j()
        })
      })
    },
  },
  [TreeNodeType.DATA_SOURCE]: {
    getChildren: (params) => {
      return new Promise((r: (value: ITreeNode[]) => void, j) => {
        connectionService.getDBList(params).then(res => {
          const data: ITreeNode[] = res.map(t => {
            return {
              key: t.name,
              name: t.name,
              treeNodeType: TreeNodeType.DATABASE,
              extraParams: {
                ...params.extraParams,
                databaseName: t.name
              }
            }
          })
          r(data);
        }).catch(error => {
          j()
        })
      })
    },
    operationColumn: [
      OperationColumn.EditSource, OperationColumn.Refresh, OperationColumn.ShiftOut
    ],
    next: TreeNodeType.DATABASE
  },
  [TreeNodeType.DATABASE]: {
    icon: '\ue62c',
    getChildren: (params: ISchemaParams) => {
      return new Promise((r: (value: ITreeNode[], b?: any) => void, j) => {
        mysqlServer.getSchemaList(params).then(res => {
          const data: ITreeNode[] = res.map(t => {
            return {
              key: t.name,
              name: t.name,
              treeNodeType: TreeNodeType.SCHEMAS,
              schemaName: t.name,
            }
          })
          r(data);
          // if (data.length) {
          // } else {
          //   let data = [
          //     {
          //       key: params.databaseName + 'tables',
          //       name: 'tables',
          //       treeNodeType: TreeNodeType.TABLES,
          //     }
          //   ]
          //   r(data, 'custom');
          // }
        }).catch(error => {
          j()
        })
      })
    },
    operationColumn: [
      OperationColumn.CreateConsole, OperationColumn.CreateTable, OperationColumn.Refresh
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
            treeNodeType: TreeNodeType.TABLES,
          }
        ]
        r(data);
      })
    },
    operationColumn: [
      OperationColumn.CreateConsole, OperationColumn.CreateTable, OperationColumn.Refresh
    ],
  },
  [TreeNodeType.TABLES]: {
    icon: '\ueac5',
    getChildren: (params) => {
      return new Promise((r: (value: ITreeNode[]) => void, j) => {
        mysqlServer.getList(params).then(res => {
          const tableList: ITreeNode[] = res.data?.map((t: any) => {
            return {
              name: t.name,
              treeNodeType: TreeNodeType.TABLE,
              key: t.name,
              pinned: t.pinned,
              extraParams: {
                ...params.extraParams,
                tableName: t.name
              }
            }
          })
          r(tableList);
        }).catch(error => {
          j()
        })
      })
    },
    operationColumn: [
      OperationColumn.CreateConsole, OperationColumn.CreateTable, OperationColumn.Refresh
    ],
  },
  [TreeNodeType.TABLE]: {
    icon: '\ue63e',
    getChildren: (params) => {
      return new Promise((r: (value: ITreeNode[]) => void, j) => {
        const tableList = [
          {
            name: 'columns',
            treeNodeType: TreeNodeType.COLUMNS,
            key: 'columns',
            extraParams: params.extraParams
          },
          {
            name: 'keys',
            treeNodeType: TreeNodeType.KEYS,
            key: 'keys',
            extraParams: params.extraParams
          },
          {
            name: 'indexs',
            treeNodeType: TreeNodeType.INDEXES,
            key: 'indexs',
            extraParams: params.extraParams
          },
        ]

        r(tableList);
      })
    },
    operationColumn: [
      OperationColumn.ExportDDL, OperationColumn.DeleteTable, OperationColumn.Top
    ],
  },
  [TreeNodeType.COLUMNS]: {
    icon: '\ueac5',
    getChildren: (params: ITableParams) => {
      return new Promise((r: (value: ITreeNode[]) => void, j) => {

        mysqlServer.getColumnList(params).then(res => {
          const tableList: ITreeNode[] = res?.map(item => {
            return {
              name: item.name,
              treeNodeType: TreeNodeType.COLUMN,
              key: item.name,
              isLeaf: true,
              columnType: item.columnType,
            }
          })
          r(tableList);
        }).catch(error => {
          j()
        })
      })
    },
    operationColumn: [OperationColumn.Refresh]
  },
  [TreeNodeType.COLUMN]: {
    icon: '\ue611'
  },
  [TreeNodeType.KEYS]: {
    icon: '\ueac5',
    getChildren: (params: ITableParams) => {
      return new Promise((r: (value: ITreeNode[]) => void, j) => {

        mysqlServer.getKeyList(params).then(res => {
          const tableList: ITreeNode[] = res?.map(item => {
            return {
              name: item.name,
              treeNodeType: TreeNodeType.KEY,
              key: item.name,
              isLeaf: true,
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
    getChildren: (params: ITableParams) => {
      return new Promise((r: (value: ITreeNode[]) => void, j) => {

        mysqlServer.getIndexList(params).then(res => {
          const tableList: ITreeNode[] = res?.map(item => {
            return {
              name: item.name,
              treeNodeType: TreeNodeType.INDEX,
              key: item.name,
              isLeaf: true,
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
  }
}