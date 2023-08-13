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
  [TreeNodeType.VIEW]: {
    icon: '\ue70c'
  },
  [TreeNodeType.FUNCTION]: {
    icon: '\ue76a'
  },
  [TreeNodeType.PROCEDURE]: {
    icon: '\ue73c'
  },
  [TreeNodeType.TRIGGER]: {
    icon: '\ue64a'
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
          const data: ITreeNode[] = res.map((t:any)=> {
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
        connectionService.getSchemaList(params).then(res => {
          const data: ITreeNode[] = res.map((t: any) => {
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
              comment: t.comment,
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
        const list = [
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

        r(list);
      })
    },
    operationColumn: [
      OperationColumn.ExportDDL, OperationColumn.DeleteTable, OperationColumn.Top
    ],
  },

  [TreeNodeType.VIEWS]: {
    icon: '\ue70c',
    getChildren: (params) => {
      return new Promise((r: (value: ITreeNode[]) => void, j) => {
        mysqlServer.getViewList(params).then(res => {
          const viewList: ITreeNode[] = res.data?.map((t: any) => {
            return {
              name: t.name,
              treeNodeType: TreeNodeType.VIEW,
              key: t.name,
              pinned: t.pinned,
              comment: t.comment,
              extraParams: {
                ...params.extraParams,
                tableName: t.name
              }
            }
          })
          r(viewList);
        }).catch(error => {
          j(error)
        })
      })
    },
  },

  [TreeNodeType.FUNCTIONS]: {
    icon: '\ue76a',
    getChildren: (params) => {
      return new Promise((r: (value: ITreeNode[]) => void, j) => {
        mysqlServer.getFunctionList(params).then(res => {
          const list: ITreeNode[] = res.data?.map((t: any) => {
            return {
              name: t.functionName,
              treeNodeType: TreeNodeType.FUNCTION,
              key: t.name,
              pinned: t.pinned,
              comment: t.comment,
              isLeaf: true,
              extraParams: {
                ...params.extraParams,
                functionName: t.functionName
              }
            }
          })
          r(list);
        }).catch(error => {
          j(error)
        })
      })
    },
  },

  [TreeNodeType.FUNCTION]: {
    icon: '\ue76a',
  },

  [TreeNodeType.PROCEDURES]: {
    icon: '\ue73c',
    getChildren: (params) => {
      return new Promise((r: (value: ITreeNode[]) => void, j) => {
        mysqlServer.getProcedureList(params).then(res => {
          const list: ITreeNode[] = res.data?.map((t: any) => {
            return {
              name: t.procedureName,
              treeNodeType: TreeNodeType.PROCEDURE,
              key: t.name,
              pinned: t.pinned,
              comment: t.comment,
              isLeaf: true,
              extraParams: {
                ...params.extraParams,
                procedureName: t.procedureName
              }
            }
          })
          r(list);
        }).catch(error => {
          j(error)
        })
      })
    },
  },

  [TreeNodeType.PROCEDURE]: {
    icon: '\ue73c'
  },

  [TreeNodeType.TRIGGERS]: {
    icon: '\ue64a',
    getChildren: (params) => {
      return new Promise((r: (value: ITreeNode[]) => void, j) => {
        mysqlServer.getTriggerList(params).then(res => {
          const list: ITreeNode[] = res.data?.map((t: any) => {
            return {
              name: t.triggerName,
              treeNodeType: TreeNodeType.TRIGGER,
              key: t.name,
              pinned: t.pinned,
              comment: t.comment,
              isLeaf: true,
              extraParams: {
                ...params.extraParams,
                triggerName: t.triggerName
              }
            }
          })
          r(list);
        }).catch(error => {
          j(error)
        })
      })
    },
  },

  [TreeNodeType.TRIGGER]: {
    icon: '\ue64a'
  },

  [TreeNodeType.VIEW]: {
    icon: '\ue70c',
    getChildren: (params) => {
      return new Promise((r: (value: ITreeNode[]) => void, j) => {
        const list = [
          {
            name: 'columns',
            treeNodeType: TreeNodeType.COLUMNS,
            key: 'columns',
            extraParams: params.extraParams
          },
        ]
        r(list);
      })
    },
  },

  [TreeNodeType.VIEWCOLUMNS]: {
    icon: '\ue647',
    getChildren: (params) => {
      return new Promise((r: (value: ITreeNode[]) => void, j) => {
        mysqlServer.getViewColumnList(params).then(res => {
          const list: ITreeNode[] = res.data?.map((t: any) => {
            return {
              name: t.name,
              treeNodeType: TreeNodeType.VIEWCOLUMN,
              key: t.name,
              pinned: t.pinned,
              comment: t.comment,
              isLeaf: true,
              extraParams: {
                ...params.extraParams,
              }
            }
          })
          r(list);
        }).catch(error => {
          j(error)
        })
      })
    },
  },
  [TreeNodeType.VIEWCOLUMN]: {
    icon: '\ue647',
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
              comment: item.comment,
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