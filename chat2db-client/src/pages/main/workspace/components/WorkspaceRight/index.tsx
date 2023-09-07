import React, { memo, useRef, useEffect, useState } from 'react';
import { connect } from 'umi';
import styles from './index.less';
import classnames from 'classnames';
import { ConsoleOpenedStatus, ConsoleStatus, DatabaseTypeCode, TreeNodeType, tabTypeConfig, TabType } from '@/constants';
import { IConsole, ICreateConsole } from '@/typings';
import historyService from '@/service/history';
import sqlService from '@/service/sql';
import Tabs, { IOption } from '@/components/Tabs';
import LoadingContent from '@/components/Loading/LoadingContent';
import ShortcutKey from '@/components/ShortcutKey';
import DatabaseTableEditor from '@/blocks/DatabaseTableEditor';
import SQLExecute from '@/blocks/SQLExecute';
import { IWorkspaceModelState, IWorkspaceModelType } from '@/models/workspace';
import { IAIState } from '@/models/ai';
import { handleLocalStorageSavedConsole } from '@/utils';
import { useUpdateEffect } from '@/hooks/useUpdateEffect';
import { v4 as uuidV4 } from 'uuid';

interface IProps {
  className?: string;
  workspaceModel: IWorkspaceModelState;
  aiModel: IAIState;
  dispatch: any;
}

export interface ITab {
  id: number;
  tabType: TabType;
  icon: string;
  [key: string]: any;
}

const WorkspaceRight = memo<IProps>(function (props) {
  const [activeConsoleId, setActiveConsoleId] = useState<number>();
  const { className, aiModel, workspaceModel, dispatch } = props;
  const { curWorkspaceParams, doubleClickTreeNodeData, createTabIntro, openConsoleList, curConsoleId } = workspaceModel;
  const openConsoleListRef = useRef(openConsoleList);
  const [tabList, setTabList] = useState<ITab[]>([]);

  useEffect(() => {
    const newTabList = openConsoleList?.map(t => {
      return {
        ...t,
        tabType: TabType.CONSOLE,
        icon: tabTypeConfig[t.operationType]?.icon || tabTypeConfig.console.icon,
      }
    })
    setTabList(newTabList || [])
  }, [openConsoleList])

  // 监听编辑表事件
  useEffect(() => {
    if (createTabIntro) {
      const id: any = uuidV4();
      setTabList([...tabList, {
        id,
        tabType: createTabIntro.tabType,
        icon: tabTypeConfig[createTabIntro.tabType as TabType]?.icon,
        name: `edit-${createTabIntro.treeNodeData.name}`,
      }])
      setActiveConsoleId(id);

      // 用完之后就清掉createTabIntro
      dispatch({
        type: 'workspace/setCreateTabIntro',
        payload: null,
      })
    }
  }, [createTabIntro])

  // 监听双击树节点事件 生成console
  useEffect(() => {
    if (!doubleClickTreeNodeData) {
      return;
    }

    dispatch({
      type: 'workspace/setConsoleList',
      payload: [],
    })

    // 打开视图
    if (doubleClickTreeNodeData.treeNodeType === TreeNodeType.VIEW) {
      const { extraParams } = doubleClickTreeNodeData;
      const { databaseName, schemaName, tableName, dataSourceId } = extraParams || {};
      const callback = (consoleId: number) => {
        sqlService.getViewDetail({
          dataSourceId: dataSourceId!,
          databaseName: databaseName!,
          tableName: tableName!,
          schemaName,
        }).then(res => {
          // 更新ddl
          const newList = openConsoleListRef.current?.map(t => {
            if (t.id === consoleId) {
              return {
                ...t,
                ddl: res.ddl
              }
            }
            return t
          })
          dispatch({
            type: 'workspace/setOpenConsoleList',
            payload: newList,
          });
        })
      }
      const name = doubleClickTreeNodeData.name;

      createConsole({
        doubleClickTreeNodeData,
        name,
        callback
      });
    }

    if (doubleClickTreeNodeData.treeNodeType === TreeNodeType.TRIGGER) {
      const { extraParams } = doubleClickTreeNodeData;
      const { databaseName, schemaName, triggerName, dataSourceId, } = extraParams || {};
      const name = doubleClickTreeNodeData.name
      const callback = (consoleId: number) => {
        sqlService.getTriggerDetail({
          dataSourceId: dataSourceId!,
          databaseName: databaseName!,
          triggerName: triggerName!,
          schemaName,
        }).then(res => {
          // 更新ddl
          const newList = openConsoleListRef.current?.map(t => {
            if (t.id === consoleId) {
              return {
                ...t,
                ddl: res.triggerBody
              }
            }
            return t
          })
          dispatch({
            type: 'workspace/setOpenConsoleList',
            payload: newList,
          });
        })
      }
      createConsole({
        doubleClickTreeNodeData,
        name,
        callback
      });
    }

    if (doubleClickTreeNodeData.treeNodeType === TreeNodeType.PROCEDURE) {
      const { extraParams } = doubleClickTreeNodeData;
      const { databaseName, schemaName, procedureName, dataSourceId } = extraParams || {};
      const name = doubleClickTreeNodeData.name
      const callback = (consoleId: number) => {
        sqlService.getProcedureDetail({
          dataSourceId: dataSourceId!,
          databaseName: databaseName!,
          procedureName: procedureName!,
          schemaName,
        }).then(res => {
          // 更新ddl
          const newList = openConsoleListRef.current?.map(t => {
            if (t.id === consoleId) {
              return {
                ...t,
                ddl: res.procedureBody
              }
            }
            return t
          })
          dispatch({
            type: 'workspace/setOpenConsoleList',
            payload: newList,
          });
        })
      }
      createConsole({
        doubleClickTreeNodeData,
        name,
        callback
      });
    }

    if (doubleClickTreeNodeData.treeNodeType === TreeNodeType.FUNCTION) {
      const { extraParams } = doubleClickTreeNodeData;
      const { databaseName, schemaName, dataSourceId, functionName } = extraParams || {};
      const name = doubleClickTreeNodeData.name
      const callback = (consoleId: number) => {
        sqlService.getFunctionDetail({
          dataSourceId: dataSourceId!,
          databaseName: databaseName!,
          functionName: functionName!,
          schemaName,
        }).then(res => {
          // 更新ddl
          const newList = openConsoleListRef.current?.map(t => {
            if (t.id === consoleId) {
              return {
                ...t,
                ddl: res.functionBody
              }
            }
            return t
          })
          dispatch({
            type: 'workspace/setOpenConsoleList',
            payload: newList,
          });
        })
      }
      createConsole({
        doubleClickTreeNodeData,
        name,
        callback
      });
    }

    if (doubleClickTreeNodeData.treeNodeType === TreeNodeType.TABLE && !openConsoleList?.length) {
      const { extraParams } = doubleClickTreeNodeData;
      const { databaseName, schemaName, tableName, } = extraParams || {};
      const ddl = `SELECT * FROM ${tableName};\n`;
      const name = [databaseName, schemaName, 'console'].filter((t) => t).join('-');
      createConsole({
        doubleClickTreeNodeData,
        name,
        ddl
      });
    }

    dispatch({
      type: 'workspace/setDoubleClickTreeNodeData',
      payload: '',
    });
  }, [doubleClickTreeNodeData]);

  useUpdateEffect(() => {
    if (activeConsoleId) {
      localStorage.setItem('active-console-id', activeConsoleId.toString())
    } else {
      localStorage.removeItem('active-console-id')
    }
  }, [activeConsoleId])

  useEffect(() => {
    openConsoleListRef.current = openConsoleList;
    const newActiveConsoleId = curConsoleId || activeConsoleId || Number(localStorage.getItem('active-console-id') || 0);
    // 用完之后就清掉curConsoleId
    if (!openConsoleList?.length) {
      setActiveConsoleId(undefined);
    } else if (!newActiveConsoleId) {
      setActiveConsoleId(openConsoleList[0].id);
    } else {
      // 如果你指定了让我打开哪个那我就打开哪个
      if (curConsoleId) {
        setActiveConsoleId(curConsoleId);
        dispatch({
          type: 'workspace/setCurConsoleId',
          payload: null,
        });
        return
      }

      let flag = false;
      openConsoleList?.forEach((t) => {
        if (t.id === newActiveConsoleId) {
          flag = true;
        }
      });
      if (flag) {
        setActiveConsoleId(newActiveConsoleId);
      } else {
        // 如果发现当前列表里并没有newActiveConsoleId
        setActiveConsoleId(openConsoleList?.[openConsoleList?.length - 1].id);
      }
    }
  }, [openConsoleList]);

  function createConsole(params: {
    doubleClickTreeNodeData: any,
    name: string,
    callback?: Function,
    ddl?: string,
  }) {
    const { doubleClickTreeNodeData, name, callback, ddl } = params;
    const { extraParams } = doubleClickTreeNodeData;
    const { databaseName, schemaName, dataSourceId, dataSourceName, databaseType } = extraParams || {};
    let p: any = {
      name,
      type: databaseType!,
      dataSourceId: dataSourceId!,
      databaseName: databaseName,
      schemaName: schemaName,
      dataSourceName: dataSourceName!,
      status: ConsoleStatus.DRAFT,
      operationType: doubleClickTreeNodeData.treeNodeType,
      ddl: ddl || '',
      tabOpened: ConsoleOpenedStatus.IS_OPEN,
    };
    addConsole({
      newConsole: p,
      callback,
    });
  }

  function getConsoleList(callback?: Function) {
    let p: any = {
      pageNo: 1,
      pageSize: 999,
      tabOpened: ConsoleOpenedStatus.IS_OPEN,
      ...curWorkspaceParams,
    };

    dispatch({
      type: 'workspace/fetchGetSavedConsole',
      payload: p,
      callback: (res: any) => {
        dispatch({
          type: 'workspace/setOpenConsoleList',
          payload: res.data,
        });
        callback?.();
      },
    });
  }

  function onChange(key: any) {
    setActiveConsoleId(key);
  }

  const onEdit = (action: 'add' | 'remove', key?: number) => {
    if (action === 'remove') {
      closeWindowTab(key!);
    }
    if (action === 'add') {
      addConsole();
    }
  };

  const addConsole = (params?: {
    newConsole?: ICreateConsole;
    callback?: Function;
  }) => {
    const { dataSourceId, databaseName, schemaName, databaseType } = curWorkspaceParams;
    let p = {
      name: `new console${openConsoleList?.length}`,
      ddl: '',
      dataSourceId: dataSourceId!,
      databaseName: databaseName!,
      schemaName: schemaName!,
      type: databaseType,
      status: ConsoleStatus.DRAFT,
      tabOpened: ConsoleOpenedStatus.IS_OPEN,
      operationType: TabType.CONSOLE,
    };
    historyService.saveConsole(params?.newConsole || p).then((res) => {
      params?.callback?.(res);
      const callback = () => {
        dispatch({
          type: 'workspace/setCurConsoleId',
          payload: res,
        });
      }
      getConsoleList(callback);
    });
  };

  const closeWindowTab = (key: number) => {
    let newActiveKey = activeConsoleId;
    let lastIndex = -1;
    openConsoleList?.forEach((item, i) => {
      if (item.id === key) {
        lastIndex = i - 1;
      }
    });

    const newPanes = openConsoleList?.filter((item) => item.id !== key) || [];
    if (newPanes.length && newActiveKey === key) {
      if (lastIndex >= 0) {
        newActiveKey = newPanes[lastIndex].id;
      } else {
        newActiveKey = newPanes[0].id;
      }
    }
    dispatch({
      type: 'workspace/setOpenConsoleList',
      payload: newPanes,
    });
    setActiveConsoleId(newActiveKey);

    let p: any = {
      id: key,
      tabOpened: 'n',
    };

    const window = openConsoleList?.find((t) => t.id === key);
    if (!window?.status) {
      return;
    }
    // if (window!.status === 'DRAFT') {
    //   historyService.deleteSavedConsole({ id: window!.id });
    // } else {
    historyService.updateSavedConsole(p).then(() => {
      handleLocalStorageSavedConsole(p.id, 'delete');
    });
    // }
  };

  function renderEmpty() {
    return <div className={styles.ears}><ShortcutKey /></div>;
  }

  function editableNameOnBlur(t: IOption) {
    let p: any = {
      id: t.value,
      name: t.label
    }
    historyService.updateSavedConsole(p).then(() => {
      getConsoleList();
      dispatch({
        type: 'workspace/fetchGetSavedConsole',
        payload: {
          pageNo: 1,
          pageSize: 999,
          orderByDesc: true,
          status: ConsoleStatus.RELEASE,
          ...curWorkspaceParams,
        },
        callback: (res: any) => {
          dispatch({
            type: 'workspace/setConsoleList',
            payload: res.data,
          })
        }
      });

    });
  }

  return (
    <div className={classnames(styles.box, className)}>
      <LoadingContent data={tabList} handleEmpty empty={renderEmpty()}>
        <div className={styles.tabBox}>
          <Tabs
            className={styles.tabs}
            onChange={onChange}
            onEdit={onEdit as any}
            editableName={true}
            editableNameOnBlur={editableNameOnBlur}
            activeTab={activeConsoleId}
            tabs={(tabList || [])?.map((t, i) => {
              return {
                prefixIcon: t.icon,
                label: t.name,
                value: t.id,
              };
            })}
          />
        </div>
        {tabList?.map((t, index) => {
          return (
            <div
              key={t.id}
              className={classnames(styles.consoleBox, { [styles.activeConsoleBox]: activeConsoleId === t.id })}
            >
              {
                [TabType.CONSOLE, TabType.FUNCTION, TabType.PROCEDURE, TabType.TRIGGER, TabType.VIEW].includes(t.tabType) && <SQLExecute
                  isActive={activeConsoleId === t.id}
                  data={{
                    initDDL: t.ddl,
                    databaseName: curWorkspaceParams.databaseName!,
                    dataSourceId: curWorkspaceParams.dataSourceId!,
                    type: curWorkspaceParams.databaseType!,
                    schemaName: curWorkspaceParams?.schemaName!,
                    consoleId: t.id,
                    consoleName: t.name,
                  }}
                  workspaceModel={workspaceModel}
                  aiModel={aiModel}
                  dispatch={dispatch}
                />
              }
              {
                t.tabType === TabType.EditTable && <DatabaseTableEditor />
              }
            </div>
          );
        })}
      </LoadingContent>
    </div >
  );
});

const dvaModel = connect(({ workspace, ai }: { workspace: IWorkspaceModelType; ai: IAIState }) => ({
  workspaceModel: workspace,
  aiModel: ai,
}));

export default dvaModel(WorkspaceRight);
