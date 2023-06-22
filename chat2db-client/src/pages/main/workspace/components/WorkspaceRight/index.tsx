import React, { memo, useRef, useEffect, useState } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import DraggableContainer from '@/components/DraggableContainer';
import Console from '@/components/Console';
import { TabOpened, ConsoleStatus, consoleTopComment } from '@/constants/common';
import { DatabaseTypeCode } from '@/constants/database';
import { IConsole } from '@/typings/common';
import historyService from '@/service/history';
import { Button, Tabs } from 'antd';
import { useReducerContext } from '@/pages/main/workspace';
import { workspaceActionType } from '@/pages/main/workspace/context';
import SearchResult from '@/components/SearchResult';
import LoadingContent from '@/components/Loading/LoadingContent';
import { IManageResultData } from '@/typings/database';

interface IProps {
  className?: string;
}

export default memo<IProps>(function WorkspaceRight(props) {
  const { className } = props;
  const draggableRef = useRef<any>();
  const [consoleList, setConsoleList] = useState<IConsole[]>();
  const [activeConsoleId, setActiveConsoleId] = useState<number>();
  const { state, dispatch } = useReducerContext();
  const { dblclickTreeNodeData, currentWorkspaceData } = state;
  const [consoleValue, setConsoleValue] = useState<string>();
  const [resultData, setResultData] = useState<IManageResultData[]>([]);

  useEffect(() => {
    getConsoleList();
  }, []);

  useEffect(() => {
    if (dblclickTreeNodeData) {
      const { extraParams } = dblclickTreeNodeData;
      const { databaseName, schemaName, dataSourceId, dataSourceName, databaseType, tableName } = extraParams || {};
      let flag = false;

      consoleList?.map((i) => {
        if (i.databaseName === databaseName && i.dataSourceId === dataSourceId) {
          flag = true;
          setActiveConsoleId(i.id);
          setConsoleValue(`SELECT * FROM ${tableName}`);
        }
      });

      if (!flag) {
        const name = [databaseName, schemaName, 'console'].filter((t) => t).join('-');
        let p = {
          name: name,
          type: databaseType,
          dataSourceId: dataSourceId,
          databaseName: databaseName,
          schemaName: schemaName,
          status: ConsoleStatus.DRAFT,
          ddl: `${consoleTopComment}`,
          tabOpened: TabOpened.IS_OPEN,
        };

        historyService.saveWindowTab(p).then((res) => {
          const newConsole: IConsole = {
            name: name,
            databaseType: databaseType!,
            databaseName: databaseName!,
            dataSourceId: dataSourceId!,
            dataSourceName: dataSourceName!,
            schemaName: schemaName!,
            id: res,
            ddl: `${consoleTopComment}`,
            status: ConsoleStatus.DRAFT,
          };
          setActiveConsoleId(newConsole.id);
          setConsoleList([...(consoleList || []), newConsole]);
          console.log([...(consoleList || []), newConsole]);
        });
      }
    }
  }, [dblclickTreeNodeData]);

  function getConsoleList() {
    let p = {
      pageNo: 1,
      pageSize: 999,
      tabOpened: TabOpened.IS_OPEN,
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
            databaseType: item.type,
            status: item.status,
            databaseName: item.databaseName,
            dataSourceName: item.dataSourceName,
            dataSourceId: item.dataSourceId,
            schemaName: item.schemaName,
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

      // if (!flag && activeConsoleId) {
      //   historyService.getWindowTab({ id: consoleId }).then((res: any) => {
      //     if (res.connectable) {
      //       newWindowList.push({
      //         id: res.id,
      //         ddl: res.ddl,
      //         name: res.name,
      //         status: res.status,
      //         databaseType: res.type,
      //         databaseName: res.databaseName,
      //         dataSourceName: res.dataSourceName,
      //         dataSourceId: res.dataSourceId,
      //         schemaName: res.schemaName,
      //       });
      //       setActiveConsoleId(res.id);
      //       setConsoleList(newWindowList);
      //     }
      //   });
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
      tabOpened: 'n',
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

  return (
    <div className={classnames(styles.box, className)}>
      <div className={styles.tabBox}>
        <Tabs
          hideAdd
          onChange={onChange}
          onEdit={onEdit}
          type="editable-card"
          items={consoleList?.map((t, i) => {
            return {
              label: t.name,
              key: t.id + '',
            };
          })}
        />
      </div>
      {consoleList?.map((t, index) => {
        return (
          <div className={classnames(styles.consoleBox, { [styles.activeConsoleBox]: activeConsoleId === t.id })}>
            <DraggableContainer layout="column" className={styles.boxRightCenter}>
              <div ref={draggableRef} className={styles.boxRightConsole}>
                <Console
                  executeParams={{
                    databaseName: currentWorkspaceData.databaseName,
                    dataSourceId: currentWorkspaceData.dataSourceId,
                    type: currentWorkspaceData.databaseType,
                    schemaName: currentWorkspaceData?.schemaName,
                    consoleId: t.id,
                    consoleName: t.name,
                  }}
                  hasAiChat={true}
                  hasAi2Lang={true}
                  value={consoleValue}
                  onExecuteSQL={(result) => {
                    console.log('onExecuteSQL', result);
                    setResultData(result);
                  }}
                />
              </div>
              <div className={styles.boxRightResult}>
                <LoadingContent data={resultData} handleEmpty>
                  <SearchResult manageResultDataList={resultData} />
                </LoadingContent>
              </div>
            </DraggableContainer>
          </div>
        );
      })}
    </div>
  );
});
