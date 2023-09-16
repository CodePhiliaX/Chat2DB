import React, { memo, useRef, useEffect, useState, useMemo } from 'react';
import { connect } from 'umi';
import styles from './index.less';
import classnames from 'classnames';
import { ConsoleOpenedStatus, ConsoleStatus, DatabaseTypeCode, TreeNodeType } from '@/constants';
import { IConsole, ICreateConsole } from '@/typings';
import historyService from '@/service/history';
import sqlService from '@/service/sql';
import Tabs, { IOption } from '@/components/Tabs';
import TabsNew, { ITabItem } from '@/components/TabsNew';
import LoadingContent from '@/components/Loading/LoadingContent';
import ShortcutKey from '@/components/ShortcutKey';
import DatabaseTableEditor from '@/blocks/DatabaseTableEditor';
import SQLExecute from '@/blocks/SQLExecute';
import { IWorkspaceModelState, IWorkspaceModelType } from '@/models/workspace';
import { IAIState } from '@/models/ai';
import { handleLocalStorageSavedConsole } from '@/utils';
import { useUpdateEffect } from '@/hooks/useUpdateEffect';
import { v4 as uuidV4 } from 'uuid';
import { IWorkspaceTab } from '@/typings'
import { WorkspaceTabType, workspaceTabConfig } from '@/constants';

interface IProps {
  className?: string;
  workspaceModel: IWorkspaceModelState;
  aiModel: IAIState;
  dispatch: any;
}

const WorkspaceRight = memo<IProps>(function (props) {
  const { className, aiModel, workspaceModel, dispatch } = props;
  // 活跃的TabID
  const [activeConsoleId, setActiveConsoleId] = useState<number | string>();
  // 工作台tab列表
  const [workspaceTabList, setWorkspaceTabList] = useState<IWorkspaceTab[]>([]);

  const { curWorkspaceParams, doubleClickTreeNodeData, createTabIntro, openConsoleList, curConsoleId } = workspaceModel;
  const openConsoleListRef = useRef(openConsoleList);

  // 根据保存的console列表生成tab列表
  useEffect(() => {
    const newTabList = openConsoleList?.map(t => {
      return {
        id: t.id,
        title: t.name,
        type: WorkspaceTabType.CONSOLE,
        uniqueData: t
      }
    })
    setWorkspaceTabList(newTabList || [])
  }, [openConsoleList])

  // 监听编辑表事件
  useEffect(() => {
    if (createTabIntro) {
      const id = uuidV4();
      const newData = {
        id,
        type: createTabIntro.workspaceTabType,
        title: `edit-${createTabIntro.treeNodeData.name}`,
        uniqueData: {
          tableName: createTabIntro.treeNodeData.name,
        }
      }
      setWorkspaceTabList([...workspaceTabList, newData])
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
      const { databaseName, schemaName, triggerName, dataSourceId } = extraParams || {};
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
    let newConsole: any = {
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
    historyService.saveConsole(newConsole).then(res => {

      setWorkspaceTabList([...workspaceTabList, {
        id: res,
        title: newConsole.name,
        type: WorkspaceTabType.CONSOLE,
        uniqueData: newConsole
      }])
      callback?.(res);
      setActiveConsoleId(res);
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

  // 切换tab
  function onTabChange(key: string | number) {
    setActiveConsoleId(key);
  }

  // 删除 新增tab
  const onEdit = (action: 'add' | 'remove', data: ITabItem) => {
    if (action === 'remove') {
      const editData = workspaceTabList?.find(t => t.id === data.key);
      if (editData?.type !== WorkspaceTabType.EditTable) {
        closeWindowTab(data.key as number);
      }
    }
    if (action === 'add') {
      addConsole();
    }
  };

  const addConsole = () => {
    const { dataSourceId, databaseName, schemaName, databaseType } = curWorkspaceParams;
    let params = {
      name: `new console`,
      ddl: '',
      dataSourceId: dataSourceId!,
      databaseName: databaseName!,
      schemaName: schemaName!,
      type: databaseType,
      status: ConsoleStatus.DRAFT,
      tabOpened: ConsoleOpenedStatus.IS_OPEN,
      operationType: WorkspaceTabType.CONSOLE,
    };
    historyService.saveConsole(params).then((res) => {
      // const callback = () => {
      //   setActiveConsoleId(res);
      //   // dispatch({
      //   //   type: 'workspace/setCurConsoleId',
      //   //   payload: res,
      //   // });
      // }
      // getConsoleList(callback);
    });
  };

  const closeWindowTab = (key: number) => {
    let p: any = {
      id: key,
      tabOpened: 'n',
    };
    // 这行干嘛的？TODO:
    // const window = openConsoleList?.find((t) => t.id === key);
    // if (!window?.status) {
    //   return;
    // }
    historyService.updateSavedConsole(p).then(() => {
      handleLocalStorageSavedConsole(p.id, 'delete');
    });
  };

  function renderEmpty() {
    return <div className={styles.ears}><ShortcutKey /></div>;
  }

  function editableNameOnBlur(t: ITabItem) {
    let p: any = {
      id: t.key,
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

  const tabsList = useMemo(() => {
    return workspaceTabList.map(t => {
      const { uniqueData } = t;
      return {
        prefixIcon: workspaceTabConfig[t.type]?.icon,
        label: t.title,
        key: t.id,
        // 这里还缺一个参数 是否可编辑tab名称, 编辑表不可编辑名称 TODO:
        children: <>
          {
            [WorkspaceTabType.CONSOLE, WorkspaceTabType.FUNCTION, WorkspaceTabType.PROCEDURE, WorkspaceTabType.TRIGGER, WorkspaceTabType.VIEW].includes(t.type) && <SQLExecute
              isActive={activeConsoleId === t.id}
              data={{
                initDDL: uniqueData?.ddl,
                databaseName: curWorkspaceParams.databaseName!,
                dataSourceId: curWorkspaceParams.dataSourceId!,
                type: curWorkspaceParams.databaseType!,
                schemaName: curWorkspaceParams?.schemaName!,
                consoleId: t.id as number,
                consoleName: uniqueData.name,
              }}
            />
          }
          {
            t.type === WorkspaceTabType.EditTable && <DatabaseTableEditor
              dataSourceId={curWorkspaceParams.dataSourceId}
              databaseName={curWorkspaceParams.databaseName!}
              schemaName={curWorkspaceParams?.schemaName!}
              tableName={uniqueData.tableName}
            />
          }
        </>
      };
    })
  }, [workspaceTabList, workspaceModel, activeConsoleId, curWorkspaceParams, aiModel, dispatch])

  return (
    <div className={classnames(styles.workspaceRight, className)}>
      <LoadingContent data={workspaceTabList} handleEmpty empty={renderEmpty()}>
        <div className={styles.tabBox}>
          <TabsNew
            className={styles.tabs}
            onChange={onTabChange}
            onEdit={onEdit as any}
            editableName={true}
            activeKey={activeConsoleId}
            editableNameOnBlur={editableNameOnBlur}
            items={tabsList}
          />
        </div>
      </LoadingContent>
    </div >
  );
});

const dvaModel = connect(({ workspace, ai }: { workspace: IWorkspaceModelType; ai: IAIState }) => ({
  workspaceModel: workspace,
  aiModel: ai,
}));

export default dvaModel(WorkspaceRight);
