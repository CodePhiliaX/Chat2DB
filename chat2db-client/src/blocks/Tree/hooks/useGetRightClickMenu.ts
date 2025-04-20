import { ITreeNode } from '@/typings';
import { OperationColumn, WorkspaceTabType, TreeNodeType } from '@/constants';
import i18n from '@/i18n';
import { v4 as uuid } from 'uuid';

// ----- components -----
import { dataSourceFormConfigs } from '@/components/ConnectionEdit/config/dataSource';
import { IConnectionConfig } from '@/components/ConnectionEdit/config/types';

// ----- config -----
import { ITreeConfigItem, treeConfig } from '../treeConfig';
import { useMemo } from 'react';

// ----- store -----
import { createConsole, addWorkspaceTab } from '@/pages/main/workspace/store/console';
import { useWorkspaceStore } from '@/pages/main/workspace/store';

// ---- functions -----
import { openView, openFunction, openProcedure, openTrigger, openSequence} from '../functions/openAsyncSql';
import { handelPinTable } from '../functions/pinTable';
import { viewDDL } from '../functions/viewDDL';
import { deleteTable } from '../functions/deleteTable';
import { deleteSequence } from '../functions/deleteSequence';

// ----- utils -----
import { compatibleDataBaseName } from '@/utils/database';

interface IProps {
  treeNodeData: ITreeNode;
  loadData: any;
}

interface IOperationColumnConfigItem {
  text: string;
  icon: string;
  doubleClickTrigger?: boolean;
  handle: (treeNodeData: ITreeNode) => void;
  discard?: boolean;
}

interface IRightClickMenu {
  key: number;
  onClick: (treeNodeData: ITreeNode) => void;
  type: OperationColumn;
  doubleClickTrigger?: boolean;
  labelProps: {
    icon: string;
    label: string;
  };
}

export const useGetRightClickMenu = (props: IProps) => {
  const { treeNodeData, loadData } = props;

  const { openCreateDatabaseModal, currentConnectionDetails } = useWorkspaceStore((state) => {
    return {
      openCreateDatabaseModal: state.openCreateDatabaseModal,
      currentConnectionDetails: state.currentConnectionDetails,
    };
  });

  const handelOpenCreateDatabaseModal = (type: 'database' | 'schema') => {

    const relyOnParams = {
      databaseType: treeNodeData.extraParams!.databaseType,
      dataSourceId: treeNodeData.extraParams!.dataSourceId!,
      databaseName: treeNodeData.name,
    }

    openCreateDatabaseModal?.({
      type,
      relyOnParams,
      executedCallback: () => {
        loadData({
          refresh: true,
        });
      },
    });
  };

  const rightClickMenu = useMemo(() => {
    // 拿出当前节点的配置
    const treeNodeConfig: ITreeConfigItem = treeConfig[treeNodeData.treeNodeType];
    const { operationColumn } = treeNodeConfig;

    const dataSourceFormConfig = dataSourceFormConfigs.find((t: IConnectionConfig) => {
      return t.type === treeNodeData.extraParams?.databaseType;
    })!;

    // 有些数据库不支持的操作，需要排除掉
    function excludeSomeOperation() {
      const excludes = dataSourceFormConfig.baseInfo.excludes;
      const newOperationColumn: OperationColumn[] = [];
      operationColumn?.map((item: OperationColumn) => {
        let flag = false;
        excludes?.map((t) => {
          if (item === t) {
            flag = true;
          }
        });
        if (!flag) {
          newOperationColumn.push(item);
        }
      });
      return newOperationColumn;
    }

    const operationColumnConfig: { [key in string]: IOperationColumnConfigItem } = {
      // 刷新
      [OperationColumn.Refresh]: {
        text: i18n('common.button.refresh'),
        icon: '\uec08',
        handle: () => {
          loadData?.({
            refresh: true,
          });
        },
      },

      // 创建console
      [OperationColumn.CreateConsole]: {
        text: i18n('workspace.menu.queryConsole'),
        icon: '\ue619',
        handle: () => {
          createConsole({
            dataSourceId: treeNodeData.extraParams!.dataSourceId!,
            dataSourceName: treeNodeData.extraParams!.dataSourceName!,
            databaseType: treeNodeData.extraParams!.databaseType!,
            databaseName: treeNodeData.extraParams?.databaseName,
            schemaName: treeNodeData.extraParams?.schemaName,
          });
        },
      },

      // 查看所有表
      [OperationColumn.ViewAllTable]: {
        text: i18n('workspace.menu.viewAllTable'),
        icon: '\ue611',
        handle: () => {
          addWorkspaceTab({
            id: uuid(),
            type: WorkspaceTabType.ViewAllTable,
            title: `${treeNodeData.extraParams!.databaseName!}-tables`,
            uniqueData: {
              dataSourceId: treeNodeData.extraParams!.dataSourceId!,
              dataSourceName: treeNodeData.extraParams!.dataSourceName!,
              databaseType: treeNodeData.extraParams!.databaseType!,
              databaseName: treeNodeData.extraParams?.databaseName,
              schemaName: treeNodeData.extraParams?.schemaName,
            },
          })
          
        },
      },

      // 创建表
      [OperationColumn.CreateTable]: {
        text: i18n('editTable.button.createTable'),
        icon: '\ue792',
        handle: () => {
          addWorkspaceTab({
            id: uuid(),
            title: i18n('editTable.button.createTable'),
            type: WorkspaceTabType.CreateTable,
            uniqueData: {
              dataSourceId: treeNodeData.extraParams!.dataSourceId!,
              databaseType: treeNodeData.extraParams!.databaseType!,
              databaseName: treeNodeData.extraParams?.databaseName,
              schemaName: treeNodeData.extraParams?.schemaName,
              submitCallback: () => {loadData?.({refresh: true})},
            },
          });
        },
        discard: (treeNodeData.treeNodeType === TreeNodeType.DATABASE && currentConnectionDetails?.supportSchema),
      },

      // 删除表
      [OperationColumn.DeleteTable]: {
        text: i18n('workspace.menu.deleteTable'),
        icon: '\ue6a7',
        handle: () => {
          deleteTable(treeNodeData,loadData);
        },
      },

      // 查看ddl
      [OperationColumn.ViewDDL]: {
        text: i18n('workspace.menu.ViewDDL'),
        icon: '\ue665',
        handle: () => {
          viewDDL(treeNodeData)
        },
      },

      // 置顶
      [OperationColumn.Pin]: {
        text: treeNodeData.pinned ? i18n('workspace.menu.unPin') : i18n('workspace.menu.pin'),
        icon: treeNodeData.pinned ? '\ue61d' : '\ue627',
        handle: () => {
          handelPinTable({
            treeNodeData,
            loadData: () => {
              loadData({treeNodeData:treeNodeData.parentNode})
            }
          });
        },
      },

      // 编辑表
      [OperationColumn.EditTable]: {
        text: i18n('workspace.menu.editTable'),
        icon: '\ue602',
        handle: () => {
          addWorkspaceTab({
            id: `${OperationColumn.EditTable}-${treeNodeData.uuid}`,
            title: treeNodeData?.name,
            type: WorkspaceTabType.EditTable,
            uniqueData: {
              dataSourceId: treeNodeData.extraParams!.dataSourceId!,
              databaseType: treeNodeData.extraParams!.databaseType!,
              databaseName: treeNodeData.extraParams?.databaseName,
              schemaName: treeNodeData.extraParams?.schemaName,
              tableName: treeNodeData?.name,
              submitCallback: () => {
                loadData({
                  treeNodeData: treeNodeData.parentNode,
                  refresh: true
                })
              },
            },
          });
        },
      },

      // 复制名称
      [OperationColumn.CopyName]: {
        text: i18n('common.button.copyName'),
        icon: '\uec7a',
        handle: () => {
          navigator.clipboard.writeText(treeNodeData.name);
        },
      },

      // 打开表
      [OperationColumn.OpenTable]: {
        text: i18n('workspace.menu.openTable'),
        icon: '\ue618',
        doubleClickTrigger: true,
        handle: () => {
          const databaseName = compatibleDataBaseName(treeNodeData.name!, treeNodeData.extraParams!.databaseType);
          addWorkspaceTab({
            id: `${OperationColumn.OpenTable}-${treeNodeData.uuid}`,
            title: treeNodeData.name,
            type: WorkspaceTabType.EditTableData,
            uniqueData: {
              dataSourceId: treeNodeData.extraParams!.dataSourceId!,
              databaseType: treeNodeData.extraParams!.databaseType!,
              databaseName: treeNodeData.extraParams?.databaseName,
              schemaName: treeNodeData.extraParams?.schemaName,
              tableName: treeNodeData.name,
              sql: 'select * from ' + databaseName,
            },
          });
        },
      },

      // 打开视图
      [OperationColumn.OpenView]: {
        text: i18n('workspace.menu.view'),
        icon: '\ue651',
        doubleClickTrigger: true,
        handle: () => {
          openView({
            addWorkspaceTab,
            treeNodeData,
          });
        },
      },

      // 打开函数
      [OperationColumn.OpenFunction]: {
        text: i18n('workspace.menu.view'),
        icon: '\ue651',
        doubleClickTrigger: true,
        handle: () => {
          openFunction({
            addWorkspaceTab,
            treeNodeData,
          });
        },
      },

      // 打开存储过程
      [OperationColumn.OpenProcedure]: {
        text: i18n('workspace.menu.view'),
        icon: '\ue651',
        doubleClickTrigger: true,
        handle: () => {
          openProcedure({
            addWorkspaceTab,
            treeNodeData,
          });
        },
      },

      // 打开触发器
      [OperationColumn.OpenTrigger]: {
        text: i18n('workspace.menu.view'),
        icon: '\ue651',
        doubleClickTrigger: true,
        handle: () => {
          openTrigger({
            addWorkspaceTab,
            treeNodeData,
          });
        },
      },

      // 打开序列
      [OperationColumn.OpenSequence]: {
        text: i18n('workspace.menu.view'),
        icon: '\ue651',
        doubleClickTrigger: true,
        handle: () => {
          openSequence({
            addWorkspaceTab,
            treeNodeData,
          });
        },
      },
      //创建序列
      [OperationColumn.CreateSequence]:{
        text: i18n('editSequence.button.createSequence'),
        icon: '\ue792',
        handle: () => {
          addWorkspaceTab({
            id: uuid(),
            title: i18n('editSequence.button.createSequence'),
            type: WorkspaceTabType.CreateSequence,
            uniqueData: {
              dataSourceId: treeNodeData.extraParams!.dataSourceId!,
              databaseType: treeNodeData.extraParams!.databaseType!,
              databaseName: treeNodeData.extraParams?.databaseName,
              schemaName: treeNodeData.extraParams?.schemaName,
              submitCallback: () => {loadData?.({refresh: true})},
            },
          });
        },
        discard: (treeNodeData.treeNodeType === TreeNodeType.SEQUENCE && currentConnectionDetails?.supportSchema),
      },
      // 编辑序列
      [OperationColumn.EditSequence]: {
        text: i18n('workspace.menu.editSequence'),
        icon: '\ue602',
        handle: () => {
          addWorkspaceTab({
            id: `${OperationColumn.EditSequence}-${treeNodeData.uuid}`,
            title: treeNodeData?.name,
            type: WorkspaceTabType.EditSequence,
            uniqueData: {
              dataSourceId: treeNodeData.extraParams!.dataSourceId!,
              databaseType: treeNodeData.extraParams!.databaseType!,
              databaseName: treeNodeData.extraParams?.databaseName,
              schemaName: treeNodeData.extraParams?.schemaName,
              tableName: treeNodeData?.name,
              submitCallback: () => {
                loadData({
                  treeNodeData: treeNodeData.parentNode,
                  refresh: true
                })
              },
            },
          });
        },
      },
      // 删除序列
      [OperationColumn.DeleteSequence]: {
        text: i18n('workspace.menu.deleteSequence'),
        icon: '\ue6a7',
        handle: () => {
          deleteSequence(treeNodeData,loadData);
        },
      },

      // 创建数据库
      [OperationColumn.CreateDatabase]: {
        text: i18n('workspace.menu.createDatabase'),
        icon: '\ue816',
        handle: () => {
          handelOpenCreateDatabaseModal('database');
        },
      },

      // 创建schema
      [OperationColumn.CreateSchema]: {
        text: i18n('workspace.menu.createSchema'),
        icon: '\ue696',
        handle: () => {
          handelOpenCreateDatabaseModal('schema');
        },
        discard: !currentConnectionDetails?.supportSchema,
      },
    };

    // 根据配置生成右键菜单
    const finalList: IRightClickMenu[] = [];
    excludeSomeOperation().forEach((t, i) => {
      const concrete = operationColumnConfig[t];
      if (!concrete.discard) {
        finalList.push({
          key: i,
          onClick: concrete?.handle,
          type: t,
          doubleClickTrigger: concrete.doubleClickTrigger,
          labelProps: {
            icon: concrete?.icon,
            label: concrete?.text,
          },
        });
      }
    });
    return finalList;
  }, [treeNodeData]);

  return rightClickMenu;
};

export const getRightClickMenu = (props: IProps) => {
  const { treeNodeData, loadData } = props;

  const openCreateDatabaseModal = useWorkspaceStore.getState().openCreateDatabaseModal;
  const currentConnectionDetails = useWorkspaceStore.getState().currentConnectionDetails;

  const handelOpenCreateDatabaseModal = (type: 'database' | 'schema') => {

    const relyOnParams = {
      databaseType: treeNodeData.extraParams!.databaseType,
      dataSourceId: treeNodeData.extraParams!.dataSourceId!,
      databaseName: treeNodeData.name,
    }

    openCreateDatabaseModal?.({
      type,
      relyOnParams,
      executedCallback: () => {
        loadData({
          refresh: true,
        });
      },
    });
  };

  // 拿出当前节点的配置
  const treeNodeConfig: ITreeConfigItem = treeConfig[treeNodeData.treeNodeType];
  const { operationColumn } = treeNodeConfig;

  const dataSourceFormConfig = dataSourceFormConfigs.find((t: IConnectionConfig) => {
    return t.type === treeNodeData.extraParams?.databaseType;
  })!;

  // 有些数据库不支持的操作，需要排除掉
  function excludeSomeOperation() {
    const excludes = dataSourceFormConfig.baseInfo.excludes;
    const newOperationColumn: OperationColumn[] = [];
    operationColumn?.map((item: OperationColumn) => {
      let flag = false;
      excludes?.map((t) => {
        if (item === t) {
          flag = true;
        }
      });
      if (!flag) {
        newOperationColumn.push(item);
      }
    });
    return newOperationColumn;
  }

  const operationColumnConfig: { [key in string]: IOperationColumnConfigItem } = {
    // 刷新
    [OperationColumn.Refresh]: {
      text: i18n('common.button.refresh'),
      icon: '\uec08',
      handle: () => {
        loadData?.({
          refresh: true,
        });
      },
    },

    // 创建console
    [OperationColumn.CreateConsole]: {
      text: i18n('workspace.menu.queryConsole'),
      icon: '\ue619',
      handle: () => {
        createConsole({
          dataSourceId: treeNodeData.extraParams!.dataSourceId!,
          dataSourceName: treeNodeData.extraParams!.dataSourceName!,
          databaseType: treeNodeData.extraParams!.databaseType!,
          databaseName: treeNodeData.extraParams?.databaseName,
          schemaName: treeNodeData.extraParams?.schemaName,
        });
      },
    },

    // 查看所有表
    [OperationColumn.ViewAllTable]: {
      text: i18n('workspace.menu.viewAllTable'),
      icon: '\ue611',
      handle: () => {
        addWorkspaceTab({
          id: uuid(),
          type: WorkspaceTabType.ViewAllTable,
          title: `${treeNodeData.extraParams!.databaseName!}-tables`,
          uniqueData: {
            dataSourceId: treeNodeData.extraParams!.dataSourceId!,
            dataSourceName: treeNodeData.extraParams!.dataSourceName!,
            databaseType: treeNodeData.extraParams!.databaseType!,
            databaseName: treeNodeData.extraParams?.databaseName,
            schemaName: treeNodeData.extraParams?.schemaName,
          },
        })
        
      },
    },

    // 创建表
    [OperationColumn.CreateTable]: {
      text: i18n('editTable.button.createTable'),
      icon: '\ue792',
      handle: () => {
        addWorkspaceTab({
          id: uuid(),
          title: i18n('editTable.button.createTable'),
          type: WorkspaceTabType.CreateTable,
          uniqueData: {
            dataSourceId: treeNodeData.extraParams!.dataSourceId!,
            databaseType: treeNodeData.extraParams!.databaseType!,
            databaseName: treeNodeData.extraParams?.databaseName,
            schemaName: treeNodeData.extraParams?.schemaName,
            submitCallback: () => {treeNodeData.loadData?.({refresh: true})},
          },
        });
      },
      discard: (treeNodeData.treeNodeType === TreeNodeType.DATABASE && currentConnectionDetails?.supportSchema),
    },

    // 删除表
    [OperationColumn.DeleteTable]: {
      text: i18n('workspace.menu.deleteTable'),
      icon: '\ue6a7',
      handle: () => {
        deleteTable(treeNodeData);
      },
    },

    // 查看ddl
    [OperationColumn.ViewDDL]: {
      text: i18n('workspace.menu.ViewDDL'),
      icon: '\ue665',
      handle: () => {
        viewDDL(treeNodeData)
      },
    },

    // 置顶
    [OperationColumn.Pin]: {
      text: treeNodeData.pinned ? i18n('workspace.menu.unPin') : i18n('workspace.menu.pin'),
      icon: treeNodeData.pinned ? '\ue61d' : '\ue627',
      handle: () => {
        handelPinTable({treeNodeData, loadData: treeNodeData.parentNode!.loadData!});
      },
    },

    // 编辑表
    [OperationColumn.EditTable]: {
      text: i18n('workspace.menu.editTable'),
      icon: '\ue602',
      handle: () => {
        addWorkspaceTab({
          id: `${OperationColumn.EditTable}-${treeNodeData.uuid}`,
          title: treeNodeData?.name,
          type: WorkspaceTabType.EditTable,
          uniqueData: {
            dataSourceId: treeNodeData.extraParams!.dataSourceId!,
            databaseType: treeNodeData.extraParams!.databaseType!,
            databaseName: treeNodeData.extraParams?.databaseName,
            schemaName: treeNodeData.extraParams?.schemaName,
            tableName: treeNodeData?.name,
            submitCallback: () => {treeNodeData.parentNode?.loadData?.({refresh: true})},
          },
        });
      },
    },

    // 复制名称
    [OperationColumn.CopyName]: {
      text: i18n('common.button.copyName'),
      icon: '\uec7a',
      handle: () => {
        navigator.clipboard.writeText(treeNodeData.name);
      },
    },

    // 打开表
    [OperationColumn.OpenTable]: {
      text: i18n('workspace.menu.openTable'),
      icon: '\ue618',
      doubleClickTrigger: true,
      handle: () => {
        const databaseName = compatibleDataBaseName(treeNodeData.name!, treeNodeData.extraParams!.databaseType);
        addWorkspaceTab({
          id: `${OperationColumn.OpenTable}-${treeNodeData.uuid}`,
          title: treeNodeData.name,
          type: WorkspaceTabType.EditTableData,
          uniqueData: {
            dataSourceId: treeNodeData.extraParams!.dataSourceId!,
            databaseType: treeNodeData.extraParams!.databaseType!,
            databaseName: treeNodeData.extraParams?.databaseName,
            schemaName: treeNodeData.extraParams?.schemaName,
            tableName: treeNodeData.name,
            sql: 'select * from ' + databaseName,
          },
        });
      },
    },

    // 打开视图
    [OperationColumn.OpenView]: {
      text: i18n('workspace.menu.view'),
      icon: '\ue651',
      doubleClickTrigger: true,
      handle: () => {
        openView({
          addWorkspaceTab,
          treeNodeData,
        });
      },
    },

    // 打开函数
    [OperationColumn.OpenFunction]: {
      text: i18n('workspace.menu.view'),
      icon: '\ue651',
      doubleClickTrigger: true,
      handle: () => {
        openFunction({
          addWorkspaceTab,
          treeNodeData,
        });
      },
    },
    
    // 打开序列
    [OperationColumn.OpenSequence]: {
      text: i18n('workspace.menu.view'),
      icon: '\ue651',
      doubleClickTrigger: true,
      handle: () => {
        openSequence({
          addWorkspaceTab,
          treeNodeData,
        });
      },
    },

    // 删除序列
    [OperationColumn.DeleteSequence]: {
      text: i18n('workspace.menu.deleteSequence'),
      icon: '\ue6a7',
      handle: () => {
        deleteSequence(treeNodeData);
      },
    },
    // 创建序列
    [OperationColumn.CreateSequence]: {
      text: i18n('editSequence.button.createSequence'),
      icon: '\ue792',
      handle: () => {
        addWorkspaceTab({
          id: uuid(),
          title: i18n('editSequence.button.createSequence'),
          type: WorkspaceTabType.CreateSequence,
          uniqueData: {
            dataSourceId: treeNodeData.extraParams!.dataSourceId!,
            databaseType: treeNodeData.extraParams!.databaseType!,
            databaseName: treeNodeData.extraParams?.databaseName,
            schemaName: treeNodeData.extraParams?.schemaName,
            submitCallback: () => {treeNodeData.loadData?.({refresh: true})},
          },
        });
      },
      discard: (treeNodeData.treeNodeType === TreeNodeType.SEQUENCES && currentConnectionDetails?.supportSchema),
    },
    // 打开存储过程
    [OperationColumn.OpenProcedure]: {
      text: i18n('workspace.menu.view'),
      icon: '\ue651',
      doubleClickTrigger: true,
      handle: () => {
        openProcedure({
          addWorkspaceTab,
          treeNodeData,
        });
      },
    },

    // 打开触发器
    [OperationColumn.OpenTrigger]: {
      text: i18n('workspace.menu.view'),
      icon: '\ue651',
      doubleClickTrigger: true,
      handle: () => {
        openTrigger({
          addWorkspaceTab,
          treeNodeData,
        });
      },
    },

    // 创建数据库
    [OperationColumn.CreateDatabase]: {
      text: i18n('workspace.menu.createDatabase'),
      icon: '\ue816',
      handle: () => {
        handelOpenCreateDatabaseModal('database');
      },
    },

    // 创建schema
    [OperationColumn.CreateSchema]: {
      text: i18n('workspace.menu.createSchema'),
      icon: '\ue696',
      handle: () => {
        handelOpenCreateDatabaseModal('schema');
      },
      discard: !currentConnectionDetails?.supportSchema,
    },
  };
  
  // 根据配置生成右键菜单
  const finalList: IRightClickMenu[] = [];
  excludeSomeOperation().forEach((t,i) => {
    const concrete = operationColumnConfig[t];
    if (!concrete.discard) {
      finalList.push({
        key: i,
        onClick: concrete?.handle,
        type: t,
        doubleClickTrigger: concrete.doubleClickTrigger,
        labelProps: {
          icon: concrete?.icon,
          label: concrete?.text,
        },
      });
    }
  });
  return finalList;
};
