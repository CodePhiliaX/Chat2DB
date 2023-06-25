import React, { memo, useRef, useEffect, useState } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import DraggableContainer from '@/components/DraggableContainer';
import Console from '@/components/Console';
import { ConsoleOpenedStatus, ConsoleStatus, consoleTopComment } from '@/constants/common';
import { DatabaseTypeCode } from '@/constants/database';
import { IConsole } from '@/typings/common';
import historyService from '@/service/history';
import { Button, Tabs } from 'antd';
import { useReducerContext } from '@/pages/main/workspace';
import { workspaceActionType } from '@/pages/main/workspace/context';
import SearchResult from '@/components/SearchResult';
import LoadingContent from '@/components/Loading/LoadingContent';
import WorkspaceRightItem from '../WorkspaceRightItem';

interface IProps {
  className?: string;
}

export default memo<IProps>(function WorkspaceRight(props) {
  const { className } = props;
  const [consoleList, setConsoleList] = useState<IConsole[]>();
  const [activeConsoleId, setActiveConsoleId] = useState<number>();
  const { state, dispatch } = useReducerContext();
  const { dblclickTreeNodeData, currentWorkspaceData } = state;

  useEffect(() => {
    getConsoleList();
  }, [currentWorkspaceData]);

  useEffect(() => {
    if(!dblclickTreeNodeData){
      return
    }
    const { extraParams } = dblclickTreeNodeData;
    const { databaseName, schemaName, dataSourceId, dataSourceName, databaseType, tableName } = extraParams || {};
    let flag = false;
    const ddl = `SELECT * FROM ${tableName};`;

    consoleList?.forEach((i) => {
      if (i.databaseName === databaseName && i.dataSourceId === dataSourceId) {
        flag = true;
        setActiveConsoleId(i.id);
      }
    });

    if (!flag) {
      const name = [databaseName, schemaName, 'console'].filter((t) => t).join('-');
      let p = {
        name: name,
        type: databaseType!,
        dataSourceId: dataSourceId!,
        databaseName: databaseName!,
        schemaName: schemaName!,
        dataSourceName: dataSourceName!,
        status: ConsoleStatus.DRAFT,
        ddl,
        tabOpened: ConsoleOpenedStatus.IS_OPEN,
        connectable: true,
      };

      historyService.saveConsole(p).then((res) => {
        const newConsole: IConsole = {
          id: res,
          ...p,
        };
        setActiveConsoleId(newConsole.id);
        setConsoleList([...(consoleList || []), newConsole]);
      });
    }
  }, [dblclickTreeNodeData]);

  function getConsoleList() {
    let p = {
      pageNo: 1,
      pageSize: 999,
      ConsoleOpenedStatus: ConsoleOpenedStatus.IS_OPEN,
      ...currentWorkspaceData,
    };

    historyService.getSaveList(p).then((res) => {
      let flag = false;
      const newWindowList: IConsole[] = [];
      res.data?.map((item, index) => {
        if (item.connectable) {
          newWindowList.push({
            id: item.id,
            ddl: item.ddl,
            name: item.name,
            type: item.type,
            status: item.status,
            databaseName: item.databaseName,
            dataSourceName: item.dataSourceName,
            dataSourceId: item.dataSourceId,
            schemaName: item.schemaName,
            connectable: true
          });
        }
      });

      newWindowList.map((item: IConsole, index: number) => {
        if (!activeConsoleId && index === 0) {
          setActiveConsoleId(item.id);
        } else if (item.id === activeConsoleId) {
          flag = true;
          setActiveConsoleId(item.id);
        }
      });

      setConsoleList(newWindowList);

      // if (!flag) {
      //   if (activeConsoleId) {
      //     historyService.getWindowTab({ id: activeConsoleId }).then((res: any) => {
      //       if (res.connectable) {
      //         newWindowList.push({
      //           id: res.id,
      //           ddl: res.ddl,
      //           name: res.name,
      //           status: res.status,
      //           type: res.type,
      //           databaseName: res.databaseName,
      //           dataSourceName: res.dataSourceName,
      //           dataSourceId: res.dataSourceId,
      //           schemaName: res.schemaName,
      //           connectable: true,
      //         });
      //         setActiveConsoleId(res.id);
      //         setConsoleList(newWindowList);
      //       }
      //     });
      //   } else {
      //     let p = {
      //       name: 'default name',
      //       ddl: 'string',
      //       dataSourceId: currentWorkspaceData.dataSourceId,
      //       databaseName: currentWorkspaceData.databaseName,
      //       type: currentWorkspaceData.databaseType,
      //       status: ConsoleStatus.DRAFT,
      //       connectable: true,
      //       tabOpened: ConsoleOpenedStatus.IS_OPEN
      //     }
      //     historyService.saveConsole(p).then(res => {
      //       setActiveConsoleId(res);
      //       // getConsoleList();
      //     })
      //   }
      // } else {
      //   setConsoleList(newWindowList);
      // }
    });
  }

  function onChange(key: string) {
    setActiveConsoleId(+key);
  }

  const onEdit = (targetKey: any, action: 'add' | 'remove') => {
    if (action === 'remove') {
      closeWindowTab(targetKey);
    }
  };

  const closeWindowTab = (targetKey: string) => {
    let newActiveKey = activeConsoleId;
    let lastIndex = -1;
    consoleList?.forEach((item, i) => {
      if (item.id === +targetKey) {
        lastIndex = i - 1;
      }
    });

    const newPanes = consoleList?.filter((item) => item.id !== +targetKey) || [];
    if (newPanes.length && newActiveKey === +targetKey) {
      if (lastIndex >= 0) {
        newActiveKey = newPanes[lastIndex].id;
      } else {
        newActiveKey = newPanes[0].id;
      }
    }
    setConsoleList(newPanes);
    setActiveConsoleId(newActiveKey);

    let p: any = {
      id: targetKey,
      ConsoleOpenedStatus: 'n',
    };

    const window = consoleList?.find((t) => t.id === +targetKey);
    if (!window?.status) {
      return;
    }
    if (window!.status === 'DRAFT') {
      historyService.deleteWindowTab({ id: window!.id });
    } else {
      historyService.updateWindowTab(p);
    }
  };

  function render() {
    return <div className={styles.ears}>
      Chat2DB
    </div>
  }

  return (
    <div className={classnames(styles.box, className)}>
      <LoadingContent data={consoleList} handleEmpty empty={render()}>
        <div className={styles.tabBox}>
          <Tabs
            hideAdd
            onChange={onChange}
            onEdit={onEdit}
            type="editable-card"
            items={(consoleList || [])?.map((t, i) => {
              return {
                label: t.name,
                key: t.id + '',
              };
            })}
          />
        </div>
        {consoleList?.map((t, index) => {
          return <div className={classnames(styles.consoleBox, { [styles.activeConsoleBox]: activeConsoleId === t.id })}>
            <WorkspaceRightItem 
              data={
                {
                  initDDL: t.ddl,
                  databaseName: currentWorkspaceData.databaseName!,
                  dataSourceId: currentWorkspaceData.dataSourceId!,
                  type: currentWorkspaceData.databaseType!,
                  schemaName: currentWorkspaceData?.schemaName!,
                  consoleId: t.id,
                  consoleName: t.name,
                }
              }
            />
          </div>
        })}
      </LoadingContent>
    </div>
  );
});
