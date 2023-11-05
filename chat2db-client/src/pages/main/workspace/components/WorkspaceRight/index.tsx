import React, { memo, useEffect, useState, useMemo, Fragment, useRef } from 'react';
import { connect } from 'umi';
import i18n from '@/i18n';
import styles from './index.less';
import classnames from 'classnames';
import { ConsoleOpenedStatus, ConsoleStatus, TreeNodeType, WorkspaceTabType, workspaceTabConfig } from '@/constants';
import historyService from '@/service/history';
import sqlService from '@/service/sql';
import Tabs, { ITabItem } from '@/components/Tabs';
import WorkspaceExtend from '../WorkspaceExtend';
import SearchResult from '@/components/SearchResult';
import Iconfont from '@/components/Iconfont';
import LoadingContent from '@/components/Loading/LoadingContent';
import ShortcutKey from '@/components/ShortcutKey';
import DatabaseTableEditor from '@/blocks/DatabaseTableEditor';
import SQLExecute from '@/blocks/SQLExecute';
import { IWorkspaceModelState, IWorkspaceModelType } from '@/models/workspace';
import { IAIState } from '@/models/ai';
import { useUpdateEffect } from '@/hooks/useUpdateEffect';
import { v4 as uuidV4 } from 'uuid';
import { IWorkspaceTab } from '@/typings';
import { Button } from 'antd';
import {
  registerIntelliSenseField,
  registerIntelliSenseKeyword,
  registerIntelliSenseTable,
} from '@/utils/IntelliSense';
import indexedDB from '@/indexedDB';
import { osNow } from '@/utils';
import { compatibleDataBaseName } from '@/utils/database';
import lodash from 'lodash';
import { registerIntelliSenseView } from '@/utils/IntelliSense/view';

interface IProps {
  className?: string;
  workspaceModel: IWorkspaceModelState;
  aiModel: IAIState;
  dispatch: any;
}

const WorkspaceRight = memo<IProps>((props: IProps) => {
  const { className, workspaceModel, dispatch } = props;
  // 活跃的TabID
  const [activeConsoleId, setActiveConsoleId] = useState<number | string | null>(null);
  // 工作台tab列表
  const [workspaceTabList, setWorkspaceTabList] = useState<IWorkspaceTab[]>([]);

  const { curWorkspaceParams, doubleClickTreeNodeData, createTabIntro, openConsoleList, createConsoleIntro } =
    workspaceModel;

  const tableList = useRef<Array<string>>([]);
  const viewList = useRef<Array<string>>([]);

  useEffect(() => {
    setActiveConsoleId(null);
    setWorkspaceTabList([]);
  }, [curWorkspaceParams]);

  // 根据保存的console列表生成tab列表
  useEffect(() => {
    const newTabList = openConsoleList?.map((t) => {
      return {
        id: t.id,
        title: t.name,
        type: t.operationType,
        uniqueData: t,
      };
    });
    if (workspaceTabList.length) {
      const newWorkspaceTabList = lodash.cloneDeep(workspaceTabList);
      const newAddList: any = [];
      newTabList.forEach((t) => {
        let flag = false;
        workspaceTabList.forEach((item, index) => {
          if (item.id === t.id) {
            flag = true;
            newWorkspaceTabList[index] = t;
          }
        });
        if (!flag) {
          newAddList.push(t);
        }
      });
      setWorkspaceTabList([...newWorkspaceTabList, ...newAddList]);
    } else {
      setWorkspaceTabList(newTabList || []);
    }
    if (!activeConsoleId) {
      setActiveConsoleId(newTabList[0]?.id);
    }
  }, [openConsoleList]);

  // 注册快捷键command+shift+L新建console
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      // 如果是mac系统
      if (osNow().isMac) {
        if (e.metaKey && e.shiftKey && e.code === 'KeyL') {
          addConsole();
        }
      } else {
        if (e.ctrlKey && e.shiftKey && e.code === 'KeyL') {
          addConsole();
        }
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => {
      window.removeEventListener('keydown', handleKeyDown);
    };
  }, [workspaceTabList]);

  useEffect(() => {
    if (createConsoleIntro) {
      if (workspaceTabList.findIndex((t) => t.id === createConsoleIntro.id) === -1) {
        setWorkspaceTabList([...workspaceTabList, createConsoleIntro]);
      }
      setActiveConsoleId(createConsoleIntro.id);
    }
    dispatch({
      type: 'workspace/setCreateConsoleIntro',
      payload: undefined,
    });
  }, [createConsoleIntro]);

  // 监听编辑表事件
  useEffect(() => {
    if (createTabIntro) {
      // 如果已经打开了这个表的编辑页面，那么就切换到这个页面
      const flag = workspaceTabList?.find(
        (t) =>
          t.uniqueData?.tableName === createTabIntro.treeNodeData.name && t.type === createTabIntro.workspaceTabType,
      );
      if (flag) {
        setActiveConsoleId(flag.id);
        return;
      }

      const id = uuidV4();
      const newData = {
        id,
        type: createTabIntro.workspaceTabType,
        title: `${createTabIntro.treeNodeData.name}`,
        uniqueData: {
          tableName: createTabIntro.treeNodeData.name,
        },
      };
      setWorkspaceTabList([...workspaceTabList, newData]);
      setActiveConsoleId(id);

      // 用完之后就清掉createTabIntro
      dispatch({
        type: 'workspace/setCreateTabIntro',
        payload: null,
      });
    }
  }, [createTabIntro]);

  // 监听双击树节点事件 生成console
  useEffect(() => {
    if (!doubleClickTreeNodeData) {
      return;
    }

    // 打开视图
    if (doubleClickTreeNodeData.treeNodeType === TreeNodeType.VIEW) {
      const { extraParams } = doubleClickTreeNodeData;
      const { databaseName, schemaName, tableName, dataSourceId } = extraParams || {};
      const callback = (consoleId: number, _workspaceTabList: IWorkspaceTab[]) => {
        sqlService
          .getViewDetail({
            dataSourceId: dataSourceId!,
            databaseName: databaseName!,
            tableName: tableName!,
            schemaName,
          })
          .then((res) => {
            // 更新ddl
            const newList = _workspaceTabList.map((t) => {
              if (t.id === consoleId) {
                return {
                  ...t,
                  uniqueData: {
                    ...t.uniqueData,
                    ddl: res.ddl,
                  },
                };
              }
              return t;
            });
            setWorkspaceTabList(newList || []);
          });
      };
      const name = doubleClickTreeNodeData.name;

      createConsole({
        doubleClickTreeNodeData,
        workSpaceTabType: WorkspaceTabType.VIEW,
        name,
        callback,
      });
    }

    if (doubleClickTreeNodeData.treeNodeType === TreeNodeType.TRIGGER) {
      const { extraParams } = doubleClickTreeNodeData;
      const { databaseName, schemaName, triggerName, dataSourceId } = extraParams || {};
      const name = doubleClickTreeNodeData.name;
      const callback = (consoleId: number, _workspaceTabList: IWorkspaceTab[]) => {
        sqlService
          .getTriggerDetail({
            dataSourceId: dataSourceId!,
            databaseName: databaseName!,
            triggerName: triggerName!,
            schemaName,
          })
          .then((res) => {
            // 更新ddl
            const newList = _workspaceTabList.map((t) => {
              if (t.id === consoleId) {
                return {
                  ...t,
                  uniqueData: {
                    ...t.uniqueData,
                    ddl: res.triggerBody,
                  },
                };
              }
              return t;
            });
            setWorkspaceTabList(newList || []);
          });
      };
      createConsole({
        doubleClickTreeNodeData,
        workSpaceTabType: WorkspaceTabType.TRIGGER,
        name,
        callback,
      });
    }

    if (doubleClickTreeNodeData.treeNodeType === TreeNodeType.PROCEDURE) {
      const { extraParams } = doubleClickTreeNodeData;
      const { databaseName, schemaName, procedureName, dataSourceId } = extraParams || {};
      const name = doubleClickTreeNodeData.name;
      const callback = (consoleId: number, _workspaceTabList: IWorkspaceTab[]) => {
        sqlService
          .getProcedureDetail({
            dataSourceId: dataSourceId!,
            databaseName: databaseName!,
            procedureName: procedureName!,
            schemaName,
          })
          .then((res) => {
            // 更新ddl
            const newList = _workspaceTabList.map((t) => {
              if (t.id === consoleId) {
                return {
                  ...t,
                  uniqueData: {
                    ...t.uniqueData,
                    ddl: res.procedureBody,
                  },
                };
              }
              return t;
            });
            setWorkspaceTabList(newList || []);
          });
      };
      createConsole({
        doubleClickTreeNodeData,
        workSpaceTabType: WorkspaceTabType.PROCEDURE,
        name,
        callback,
      });
    }

    if (doubleClickTreeNodeData.treeNodeType === TreeNodeType.FUNCTION) {
      const { extraParams } = doubleClickTreeNodeData;
      const { databaseName, schemaName, dataSourceId, functionName } = extraParams || {};
      const name = doubleClickTreeNodeData.name;
      const callback = (consoleId: number, _workspaceTabList: IWorkspaceTab[]) => {
        sqlService
          .getFunctionDetail({
            dataSourceId: dataSourceId!,
            databaseName: databaseName!,
            functionName: functionName!,
            schemaName,
          })
          .then((res) => {
            // 更新ddl
            const newList = _workspaceTabList?.map((t) => {
              if (t.id === consoleId) {
                return {
                  ...t,
                  uniqueData: {
                    ...t.uniqueData,
                    ddl: res.functionBody,
                  },
                };
              }
              return t;
            });
            setWorkspaceTabList(newList || []);
          });
      };
      createConsole({
        doubleClickTreeNodeData,
        workSpaceTabType: WorkspaceTabType.FUNCTION,
        name,
        callback,
      });
    }

    if (doubleClickTreeNodeData.treeNodeType === TreeNodeType.TABLE) {
      const { extraParams } = doubleClickTreeNodeData;
      const { tableName } = extraParams || {};
      const sql = `SELECT * FROM ${compatibleDataBaseName(tableName!, curWorkspaceParams.databaseType)};\n`;
      const title = tableName!;
      const id = uuidV4();
      let flag = false;
      workspaceTabList.forEach((t) => {
        if (t.uniqueData?.sql === sql) {
          setActiveConsoleId(t.id);
          flag = true;
          return;
        }
      });
      if (!flag) {
        setWorkspaceTabList([
          ...(workspaceTabList || []),
          {
            id,
            title,
            type: WorkspaceTabType.EditTableData,
            uniqueData: {
              sql,
            },
          },
        ]);
        setActiveConsoleId(id);
      }
    }

    dispatch({
      type: 'workspace/setDoubleClickTreeNodeData',
      payload: '',
    });
  }, [doubleClickTreeNodeData]);

  useUpdateEffect(() => {
    if (activeConsoleId) {
      localStorage.setItem('active-console-id', activeConsoleId.toString());
    } else {
      localStorage.removeItem('active-console-id');
    }
  }, [activeConsoleId]);

  // 更新关键字提示
  useEffect(() => {
    if (curWorkspaceParams.databaseType) {
      registerIntelliSenseKeyword(curWorkspaceParams.databaseType);
    }
  }, [curWorkspaceParams.databaseType]);

  // 更新表名提示
  useUpdateEffect(() => {
    const { dataSourceId, databaseName, schemaName, databaseType } = curWorkspaceParams;
    if (dataSourceId === null || dataSourceId === undefined) {
      return;
    }
    sqlService
      .getAllTableList({
        dataSourceId,
        databaseName,
        schemaName,
      })
      .then((data) => {
        tableList.current = (data || []).map((item: any) => item.name);
        registerIntelliSenseTable(data, databaseType, dataSourceId, databaseName, schemaName);
        registerIntelliSenseField(tableList.current, dataSourceId, databaseName, schemaName);
      });
  }, [workspaceModel.curTableList]); //当curTableList变化时（比如手动刷新，切换databaseName、schemaName），重新注册表名提示

  useEffect(() => {
    const { dataSourceId, dataSourceName, databaseName, schemaName, databaseType } = curWorkspaceParams;
    // debugger
    if (!dataSourceId || !(databaseName || schemaName)) {
      return;
    }
    sqlService
      .getViewList({
        dataSourceId,
        dataSourceName,
        databaseName,
        schemaName,
        databaseType,
      })
      .then((res) => {
        viewList.current = (res.data || []).map((item: any) => item.name);
        registerIntelliSenseView(viewList.current, databaseName);
      });
  }, [curWorkspaceParams.dataSourceId, curWorkspaceParams.databaseName, curWorkspaceParams.schemaName]);

  function createConsole(params: {
    doubleClickTreeNodeData: any;
    workSpaceTabType: WorkspaceTabType;
    name: string;
    callback?: (res: number, list: any) => void;
    ddl?: string;
  }) {
    const { doubleClickTreeNodeData: _doubleClickTreeNodeData, workSpaceTabType, name, callback, ddl } = params;
    const { extraParams } = _doubleClickTreeNodeData;
    const { databaseName, schemaName, dataSourceId, dataSourceName, databaseType } = extraParams || {};
    const newConsole: any = {
      name,
      type: databaseType!,
      dataSourceId: dataSourceId!,
      databaseName: databaseName,
      schemaName: schemaName,
      dataSourceName: dataSourceName!,
      status: ConsoleStatus.DRAFT,
      operationType: workSpaceTabType,
      ddl: ddl || '',
      tabOpened: ConsoleOpenedStatus.IS_OPEN,
    };
    historyService.saveConsole(newConsole).then((res) => {
      const newList = [
        ...workspaceTabList,
        {
          id: res,
          title: newConsole.name,
          type: workSpaceTabType,
          uniqueData: newConsole,
        },
      ];
      setWorkspaceTabList(newList);
      callback?.(res, newList);
      setActiveConsoleId(res);
    });
  }

  function getConsoleList(callback?: () => void) {
    const p: any = {
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
  function onTabChange(key: string | number | null) {
    setActiveConsoleId(key);
  }

  // 删除 新增tab
  const onEdit = (action: 'add' | 'remove', data: ITabItem[]) => {
    if (action === 'remove') {
      setWorkspaceTabList(
        workspaceTabList.filter((t) => {
          return data.findIndex((item) => item.key === t.id) === -1;
        }),
      );
      data.forEach((item) => {
        const editData = workspaceTabList?.find((t) => t.id === item.key);
        if (
          editData?.type !== WorkspaceTabType.EditTable &&
          editData?.type !== WorkspaceTabType.CreateTable &&
          editData?.type !== WorkspaceTabType.EditTableData
        ) {
          closeWindowTab(item.key as number);
        }
      })

      
    }
    if (action === 'add') {
      addConsole();
    }
  };

  const addConsole = () => {
    const { dataSourceId, databaseName, schemaName, databaseType } = curWorkspaceParams;
    const newConsole = {
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
    historyService.saveConsole(newConsole).then((res) => {
      const newList = [
        ...workspaceTabList,
        {
          id: res,
          title: newConsole.name,
          type: newConsole.operationType,
          uniqueData: newConsole,
        },
      ];
      setWorkspaceTabList(newList);
      setActiveConsoleId(res);
    });
  };

  const closeWindowTab = (key: number) => {
    const p: any = {
      id: key,
      tabOpened: 'n',
    };

    historyService.updateSavedConsole(p).then(() => {
      indexedDB.deleteData('chat2db', 'workspaceConsoleDDL', key);
    });
  };

  function renderCreateConsoleButton() {
    return (
      <div className={styles.createButtonBox}>
        <Button className={styles.createButton} type="primary" onClick={addConsole}>
          <Iconfont code="&#xe63a;" />
          {i18n('common.button.createConsole')}
        </Button>
      </div>
    );
  }

  function renderEmpty() {
    return (
      <div className={styles.ears}>
        <ShortcutKey slot={renderCreateConsoleButton} />
      </div>
    );
  }

  function editableNameOnBlur(t: ITabItem) {
    const _params: any = {
      id: t.key,
      name: t.label,
    };
    historyService.updateSavedConsole(_params).then(() => {
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
          });
        },
      });
    });
  }

  const changeTabDetails = (data: IWorkspaceTab) => {
    const list = workspaceTabList.map((t) => {
      if (t.id === data.id) {
        return data;
      }
      return t;
    });
    setWorkspaceTabList(list);
  };

  const tabsList = useMemo(() => {
    return workspaceTabList.map((t) => {
      const { uniqueData } = t;
      return {
        prefixIcon: workspaceTabConfig[t.type]?.icon,
        label: t.title,
        key: t.id,
        editableName: t.type === WorkspaceTabType.CONSOLE,
        children: (
          <Fragment key={t.id}>
            {[
              WorkspaceTabType.CONSOLE,
              WorkspaceTabType.FUNCTION,
              WorkspaceTabType.PROCEDURE,
              WorkspaceTabType.TRIGGER,
              WorkspaceTabType.VIEW,
            ].includes(t.type) && (
              <SQLExecute
                isActive={activeConsoleId === t.id}
                data={{
                  initDDL: uniqueData?.ddl,
                  dataSourceId: curWorkspaceParams.dataSourceId!,
                  databaseName: curWorkspaceParams.databaseName!,
                  type: curWorkspaceParams.databaseType!,
                  schemaName: curWorkspaceParams?.schemaName,
                  consoleId: t.id as number,
                  consoleName: uniqueData.name,
                  status: uniqueData.status,
                }}
              />
            )}
            {(t.type === WorkspaceTabType.EditTable || t.type === WorkspaceTabType.CreateTable) && (
              <DatabaseTableEditor
                tabDetails={t}
                changeTabDetails={changeTabDetails}
                dataSourceId={curWorkspaceParams.dataSourceId}
                databaseName={curWorkspaceParams.databaseName!}
                databaseType={curWorkspaceParams?.databaseType}
                schemaName={curWorkspaceParams?.schemaName}
                tableName={uniqueData.tableName}
              />
            )}
            {t.type === WorkspaceTabType.EditTableData && (
              <SearchResult sql={uniqueData.sql} executeSqlParams={curWorkspaceParams} />
            )}
          </Fragment>
        ),
      };
    });
  }, [workspaceTabList, activeConsoleId, curWorkspaceParams]);

  return (
    <div className={classnames(styles.workspaceRight, className)}>
      <LoadingContent className={styles.workspaceRightMain} data={workspaceTabList} handleEmpty empty={renderEmpty()}>
        <div className={styles.tabBox}>
          <Tabs
            className={styles.tabs}
            onChange={onTabChange}
            onEdit={onEdit as any}
            activeKey={activeConsoleId}
            editableNameOnBlur={editableNameOnBlur}
            items={tabsList}
          />
        </div>
        <WorkspaceExtend curWorkspaceParams={curWorkspaceParams} className={styles.workspaceExtend} />
      </LoadingContent>
    </div>
  );
});

const dvaModel = connect(({ workspace, ai }: { workspace: IWorkspaceModelType; ai: IAIState }) => ({
  workspaceModel: workspace,
  aiModel: ai,
}));

export default dvaModel(WorkspaceRight);
