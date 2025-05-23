import { ITreeNode, IConnectionDetails } from '@/typings';
import { TreeNodeType, OperationColumn } from '@/constants';
import connectionService from '@/service/connection';
import { v4 as uuid } from 'uuid';

import mysqlServer from '@/service/sql';

export type ITreeConfig = Partial<{ [key in TreeNodeType]: ITreeConfigItem }>;

export const switchIcon: Partial<{ [key in TreeNodeType]: { icon: string; unfoldIcon?: string } }> = {
  [TreeNodeType.DATABASE]: {
    icon: '\ue669',
  },
  [TreeNodeType.SCHEMAS]: {
    icon: '\ue696',
  },
  [TreeNodeType.TABLE]: {
    icon: '\ue63e',
  },
  [TreeNodeType.TABLES]: {
    icon: '\ueabe',
    unfoldIcon: '\ueabf',
  },
  [TreeNodeType.COLUMNS]: {
    icon: '\ueabe',
    unfoldIcon: '\ueabf',
  },
  [TreeNodeType.COLUMN]: {
    icon: '\ue611',
  },
  [TreeNodeType.KEYS]: {
    icon: '\ueabe',
    unfoldIcon: '\ueabf',
  },
  [TreeNodeType.KEY]: {
    icon: '\ue775',
  },
  [TreeNodeType.INDEXES]: {
    icon: '\ueabe',
    unfoldIcon: '\ueabf',
  },
  [TreeNodeType.INDEX]: {
    icon: '\ue65b',
  },
  [TreeNodeType.VIEWS]: {
    icon: '\ueabe',
    unfoldIcon: '\ueabf',
  },
  [TreeNodeType.VIEW]: {
    icon: '\ue70c',
  },
  [TreeNodeType.FUNCTION]: {
    icon: '\ue76a',
  },
  [TreeNodeType.PROCEDURE]: {
    icon: '\ue73c',
  },
  [TreeNodeType.TRIGGER]: {
    icon: '\ue64a',
  },
  [TreeNodeType.VIEWCOLUMNS]: {
    icon: '\ueabe',
    unfoldIcon: '\ueabf',
  },
  [TreeNodeType.VIEWCOLUMN]: {
    icon: '\ue647',
  },
  [TreeNodeType.FUNCTIONS]: {
    icon: '\ueabe',
    unfoldIcon: '\ueabf',
  },
  [TreeNodeType.PROCEDURES]: {
    icon: '\ueabe',
    unfoldIcon: '\ueabf',
  },
  [TreeNodeType.TRIGGERS]: {
    icon: '\ueabe',
    unfoldIcon: '\ueabf',
  },
  [TreeNodeType.SEQUENCES]: {
    icon: '\ueabe',
    unfoldIcon: '\ueabf',
  },
  [TreeNodeType.SEQUENCE]: {
    icon: '\ue611',
  },
};

export interface ITreeConfigItem {
  icon?: string;
  getChildren?: (params: any, options?: any) => Promise<ITreeNode[]>;
  next?: TreeNodeType;
  operationColumn?: OperationColumn[];
}

export const treeConfig: { [key in TreeNodeType]: ITreeConfigItem } = {
  [TreeNodeType.DATA_SOURCES]: {
    getChildren: () => {
      return new Promise((r: (value: ITreeNode[]) => void, j) => {
        const p = {
          pageNo: 1,
          pageSize: 1000,
        };
        connectionService
          .getList(p)
          .then((res) => {
            const data: ITreeNode[] = res.data.map((t: IConnectionDetails) => {
              return {
                uuid: uuid(),
                key: t.id,
                name: t.alias,
                treeNodeType: TreeNodeType.DATA_SOURCE,
                extraParams: {
                  databaseType: t.type,
                  dataSourceId: t.id,
                  dataSourceName: t.alias,
                },
              };
            });
            r(data);
          })
          .catch(() => {
            j();
          });
      });
    },
  },

  [TreeNodeType.DATA_SOURCE]: {
    getChildren: (params: { dataSourceId: number; dataSourceName: string; extraParams: any }) => {
      return new Promise((r, j) => {
        const _extraParams = params.extraParams;
        delete params.extraParams;
        connectionService
          .getDatabaseList(params)
          .then((res) => {
            const data: ITreeNode[] = res.map((t: any) => {
              return {
                uuid: uuid(),
                key: t.name,
                name: t.name,
                treeNodeType: TreeNodeType.DATABASE,
                extraParams: {
                  ..._extraParams,
                  databaseName: t.name,
                },
              };
            });
            r(data);
          })
          .catch(() => {
            j();
          });
      });
    },
    operationColumn: [OperationColumn.EditSource, OperationColumn.Refresh, OperationColumn.ShiftOut],
    next: TreeNodeType.DATABASE,
  },

  [TreeNodeType.DATABASE]: {
    icon: '\ue62c',
    getChildren: (params) => {
      const _extraParams = params.extraParams;
      delete params.extraParams;
      return new Promise((r: (value: ITreeNode[], b?: any) => void, j) => {
        connectionService
          .getSchemaList(params)
          .then((res) => {
            const data: ITreeNode[] = res.map((t: any) => {
              return {
                uuid: uuid(),
                key: t.name,
                name: t.name,
                treeNodeType: TreeNodeType.SCHEMAS,
                schemaName: t.name,
                extraParams: {
                  ..._extraParams,
                  schemaName: t.name,
                },
              };
            });
            r(data);
          })
          .catch(() => {
            j();
          });
      });
    },
    operationColumn: [
      OperationColumn.CreateConsole,
      OperationColumn.CreateSchema,
      // OperationColumn.CreateTable,
      OperationColumn.CopyName,
      OperationColumn.Refresh,
    ],
    next: TreeNodeType.SCHEMAS,
  },

  [TreeNodeType.SCHEMAS]: {
    icon: '\ue696',
    getChildren: (parentData: ITreeNode) => {
      const { dataSourceId, databaseName, schemaName } = parentData.extraParams!;
      const preCode = [dataSourceId, databaseName, schemaName].join('-');
      return new Promise((r: (value: ITreeNode[]) => void) => {
        const data = [
          {
            uuid: uuid(),
            key: `${preCode}-tables`,
            name: 'tables',
            treeNodeType: TreeNodeType.TABLES,
            extraParams: parentData.extraParams,
          },
          {
            uuid: uuid(),
            key: `${preCode}-views`,
            name: 'view',
            treeNodeType: TreeNodeType.VIEWS,
            extraParams: parentData.extraParams,
          },
          {
            uuid: uuid(),
            key: `${preCode}-functions`,
            name: 'functions',
            treeNodeType: TreeNodeType.FUNCTIONS,
            extraParams: parentData.extraParams,
          },
          {
            uuid: uuid(),
            key: `${preCode}-procedures`,
            name: 'procedures',
            treeNodeType: TreeNodeType.PROCEDURES,
            extraParams: parentData.extraParams,
          },
          {
            uuid: uuid(),
            key: `${preCode}-triggers`,
            name: 'triggers',
            treeNodeType: TreeNodeType.TRIGGERS,
            extraParams: parentData.extraParams,
          },
        ];
        if((parentData.extraParams?.databaseType === 'POSTGRESQL'|| parentData.extraParams?.databaseType === 'ORACLE')&& schemaName==='public'){
          data.push({
            uuid: uuid(),
            key: `${preCode}-sequences`,
            name: 'sequences',
            treeNodeType: TreeNodeType.SEQUENCES,
            extraParams: parentData.extraParams,
          });
        }
        r(data);
      });
    },
    operationColumn: [OperationColumn.CreateConsole, OperationColumn.Refresh],
  },

  [TreeNodeType.TABLES]: {
    icon: '\ueac5',
    getChildren: (params, options) => {
      const _extraParams = params.extraParams;
      delete params.extraParams;
      params.pageSize = 1000;
      return new Promise((r, j) => {
        mysqlServer
          .getTableList(params, options)
          .then((res) => {
            const tableList: ITreeNode[] = res.data?.map((t: any) => {
              return {
                uuid: uuid(),
                name: t.name,
                treeNodeType: TreeNodeType.TABLE,
                key: t.name,
                pinned: t.pinned,
                comment: t.comment,
                extraParams: {
                  ..._extraParams,
                  tableName: t.name,
                },
              };
            });
            r({
              data: tableList,
              pageNo: res.pageNo,
              pageSize: res.pageSize,
              total: res.total,
              hasNextPage: res.hasNextPage,
            } as any);
          })
          .catch((error) => {
            j(error);
          });
      });
    },
    operationColumn: [
      OperationColumn.CreateConsole,
      OperationColumn.ViewAllTable,
      OperationColumn.CreateTable,
      OperationColumn.Refresh,
    ],
  },

  [TreeNodeType.TABLE]: {
    icon: '\ue63e',
    getChildren: (params) => {
      return new Promise((r: (value: ITreeNode[]) => void) => {
        const { dataSourceId, databaseName, schemaName, tableName } = params.extraParams!;
        const preCode = [dataSourceId, databaseName, schemaName, tableName].join('-');
        const list = [
          {
            uuid: uuid(),
            key: `${preCode}-columns`,
            name: 'columns',
            treeNodeType: TreeNodeType.COLUMNS,
            extraParams: params.extraParams,
          },
          {
            uuid: uuid(),
            key: `${preCode}-keys`,
            name: 'keys',
            treeNodeType: TreeNodeType.KEYS,
            extraParams: params.extraParams,
          },
          {
            uuid: uuid(),
            key: `${preCode}-indexs`,
            name: 'indexs',
            treeNodeType: TreeNodeType.INDEXES,
            extraParams: params.extraParams,
          },
        ];

        r(list);
      });
    },
    operationColumn: [
      OperationColumn.OpenTable,
      OperationColumn.CreateConsole,
      OperationColumn.Pin,
      OperationColumn.ViewDDL,
      OperationColumn.EditTable,
      OperationColumn.CopyName,
      OperationColumn.Refresh,
      OperationColumn.DeleteTable,
    ],
  },

  [TreeNodeType.VIEWS]: {
    icon: '\ue70c',
    getChildren: (params) => {
      const _extraParams = params.extraParams;
      delete params.extraParams;
      return new Promise((r: (value: ITreeNode[]) => void, j) => {
        mysqlServer
          .getViewList(params)
          .then((res) => {
            const viewList: ITreeNode[] = res.data?.map((t: any) => {
              return {
                uuid: uuid(),
                name: t.name,
                treeNodeType: TreeNodeType.VIEW,
                key: t.name,
                pinned: t.pinned,
                comment: t.comment,
                extraParams: {
                  ..._extraParams,
                  tableName: t.name,
                },
              };
            });
            r(viewList);
          })
          .catch((error) => {
            j(error);
          });
      });
    },
    operationColumn: [OperationColumn.CreateConsole, OperationColumn.Refresh],
  },

  [TreeNodeType.FUNCTIONS]: {
    icon: '\ue76a',
    getChildren: (params) => {
      const _extraParams = params.extraParams;
      delete params.extraParams;
      return new Promise((r: (value: ITreeNode[]) => void, j) => {
        mysqlServer
          .getFunctionList(params)
          .then((res) => {
            const list: ITreeNode[] = res.data?.map((t: any) => {
              return {
                uuid: uuid(),
                name: t.functionName,
                treeNodeType: TreeNodeType.FUNCTION,
                key: t.name,
                pinned: t.pinned,
                comment: t.comment,
                isLeaf: true,
                extraParams: {
                  ..._extraParams,
                  functionName: t.functionName,
                },
              };
            });
            r(list);
          })
          .catch((error) => {
            j(error);
          });
      });
    },
    operationColumn: [OperationColumn.CreateConsole, OperationColumn.Refresh],
  },

  [TreeNodeType.FUNCTION]: {
    icon: '\ue76a',
    operationColumn: [OperationColumn.CreateConsole, OperationColumn.OpenFunction, OperationColumn.CopyName],
  },

  [TreeNodeType.PROCEDURES]: {
    icon: '\ue73c',
    getChildren: (params) => {
      const _extraParams = params.extraParams;
      delete params.extraParams;
      return new Promise((r: (value: ITreeNode[]) => void, j) => {
        mysqlServer
          .getProcedureList(params)
          .then((res) => {
            const list: ITreeNode[] = res.data?.map((t: any) => {
              return {
                uuid: uuid(),
                name: t.procedureName,
                treeNodeType: TreeNodeType.PROCEDURE,
                key: t.name,
                pinned: t.pinned,
                comment: t.comment,
                isLeaf: true,
                extraParams: {
                  ..._extraParams,
                  procedureName: t.procedureName,
                },
              };
            });
            r(list);
          })
          .catch((error) => {
            j(error);
          });
      });
    },
    operationColumn: [OperationColumn.CreateConsole, OperationColumn.Refresh],
  },

  [TreeNodeType.PROCEDURE]: {
    icon: '\ue73c',
    operationColumn: [OperationColumn.CreateConsole, OperationColumn.OpenProcedure, OperationColumn.CopyName],
  },

  [TreeNodeType.TRIGGERS]: {
    icon: '\ue64a',
    getChildren: (params) => {
      const _extraParams = params.extraParams;
      delete params.extraParams;
      return new Promise((r: (value: ITreeNode[]) => void, j) => {
        mysqlServer
          .getTriggerList(params)
          .then((res) => {
            const list: ITreeNode[] = res.data?.map((t: any) => {
              return {
                uuid: uuid(),
                name: t.triggerName,
                treeNodeType: TreeNodeType.TRIGGER,
                key: t.name,
                pinned: t.pinned,
                comment: t.comment,
                isLeaf: true,
                extraParams: {
                  ..._extraParams,
                  triggerName: t.triggerName,
                },
              };
            });
            r(list);
          })
          .catch((error) => {
            j(error);
          });
      });
    },
    operationColumn: [OperationColumn.CreateConsole, OperationColumn.Refresh],
  },

  [TreeNodeType.TRIGGER]: {
    icon: '\ue64a',
    operationColumn: [OperationColumn.CreateConsole, OperationColumn.OpenTrigger, OperationColumn.CopyName],
  },

  [TreeNodeType.VIEW]: {
    icon: '\ue70c',
    getChildren: (params) => {
      return new Promise((r: (value: ITreeNode[]) => void) => {
        const list = [
          {
            uuid: uuid(),
            name: 'columns',
            treeNodeType: TreeNodeType.COLUMNS,
            key: 'columns',
            extraParams: params.extraParams,
          },
        ];
        r(list);
      });
    },
    operationColumn: [OperationColumn.CreateConsole, OperationColumn.OpenView, OperationColumn.CopyName],
  },

  [TreeNodeType.VIEWCOLUMNS]: {
    icon: '\ue647',
    getChildren: (params) => {
      const _extraParams = params.extraParams;
      delete params.extraParams;
      return new Promise((r: (value: ITreeNode[]) => void, j) => {
        mysqlServer
          .getViewColumnList(params)
          .then((res) => {
            const list: ITreeNode[] = res.data?.map((t: any) => {
              return {
                uuid: uuid(),
                name: t.name,
                treeNodeType: TreeNodeType.VIEWCOLUMN,
                key: t.name,
                pinned: t.pinned,
                comment: t.comment,
                isLeaf: true,
                extraParams: _extraParams,
              };
            });
            r(list);
          })
          .catch((error) => {
            j(error);
          });
      });
    },
    operationColumn: [OperationColumn.CreateConsole, OperationColumn.CopyName, OperationColumn.Refresh],
  },

  [TreeNodeType.VIEWCOLUMN]: {
    icon: '\ue647',
    operationColumn: [OperationColumn.CreateConsole, OperationColumn.CopyName],
  },

  [TreeNodeType.COLUMNS]: {
    icon: '\ueac5',
    getChildren: (params) => {
      const _extraParams = params.extraParams;
      delete params.extraParams;
      return new Promise((r: (value: ITreeNode[]) => void, j) => {
        mysqlServer
          .getColumnList(params)
          .then((res) => {
            const tableList: ITreeNode[] = res?.map((item) => {
              return {
                uuid: uuid(),
                name: item.name,
                treeNodeType: TreeNodeType.COLUMN,
                key: item.name,
                isLeaf: true,
                columnType: item.columnType,
                comment: item.comment,
                extraParams: _extraParams,
              };
            });
            r(tableList);
          })
          .catch(() => {
            j();
          });
      });
    },
    operationColumn: [OperationColumn.CreateConsole, OperationColumn.Refresh],
  },
  [TreeNodeType.COLUMN]: {
    icon: '\ue611',
    operationColumn: [OperationColumn.CreateConsole, OperationColumn.CopyName],
  },
  [TreeNodeType.KEYS]: {
    icon: '\ueac5',
    getChildren: (params) => {
      const _extraParams = params.extraParams;
      delete params.extraParams;
      return new Promise((r: (value: ITreeNode[]) => void, j) => {
        mysqlServer
          .getKeyList(params)
          .then((res) => {
            const tableList: ITreeNode[] = res?.map((item) => {
              return {
                uuid: uuid(),
                name: item.name,
                treeNodeType: TreeNodeType.KEY,
                key: item.name,
                isLeaf: true,
                extraParams: _extraParams,
              };
            });
            r(tableList);
          })
          .catch(() => {
            j();
          });
      });
    },
    operationColumn: [OperationColumn.CreateConsole, OperationColumn.CopyName, OperationColumn.Refresh],
  },
  [TreeNodeType.KEY]: {
    icon: '\ue775',
    operationColumn: [OperationColumn.CreateConsole, OperationColumn.CopyName],
  },
  [TreeNodeType.INDEXES]: {
    icon: '\ueac5',
    getChildren: (params) => {
      const _extraParams = params.extraParams;
      delete params.extraParams;
      return new Promise((r: (value: ITreeNode[]) => void, j) => {
        mysqlServer
          .getIndexList(params)
          .then((res) => {
            const tableList: ITreeNode[] = res?.map((item) => {
              return {
                uuid: uuid(),
                name: item.name,
                treeNodeType: TreeNodeType.INDEX,
                key: item.name,
                isLeaf: true,
                extraParams: _extraParams,
              };
            });
            r(tableList);
          })
          .catch(() => {
            j();
          });
      });
    },
    operationColumn: [OperationColumn.CreateConsole, OperationColumn.CopyName, OperationColumn.Refresh],
  },
  [TreeNodeType.INDEX]: {
    icon: '\ue65b',
    operationColumn: [OperationColumn.CreateConsole, OperationColumn.CopyName],
  },
  [TreeNodeType.SEQUENCES]: {
    icon: '\ueabe', // 使用现有图标（如文件夹折叠）
   
    getChildren: (params) => {
      const _extraParams = params.extraParams;
      delete params.extraParams;
      return new Promise((r: (value: ITreeNode[]) => void, j) => {
        mysqlServer
          .getSequenceList(params)
          .then((res) => {
            const data: ITreeNode[] = res?.map((item:any) => {
              return {
                uuid: uuid(),
                key: item.name,
                name: item.name,
                treeNodeType: TreeNodeType.SEQUENCE,
                sequenceName: item.name,
                isLeaf: true,
                extraParams: {
                  ..._extraParams,
                  sequenceName: item.name,
                },
              };
            });
            r(data);
          })
          .catch((error) => {
            j(error);
          });
      })
    },
    operationColumn: [
      OperationColumn.CreateSequence,
      OperationColumn.CopyName,
      OperationColumn.Refresh,
    ],
  },
  [TreeNodeType.SEQUENCE]: {
    icon: '\ue611',
    operationColumn: [OperationColumn.OpenSequence, OperationColumn.EditSequence, OperationColumn.CopyName,OperationColumn.DeleteSequence],
  },
};
