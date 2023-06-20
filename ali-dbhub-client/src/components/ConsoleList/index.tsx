import React, { memo, useEffect, useState, useContext } from 'react';
import classnames from 'classnames';
import ModifyTable from '@/components/ModifyTable/ModifyTable';
import DatabaseQuery from '@/components/DatabaseQuery';
import AppHeader from '@/components/AppHeader';
import { qs } from '@/utils';
import { Tabs } from 'antd';

import historyService from '@/service/history';

import {
  ISavedConsole,
  IConsole,
  IEditTableConsole,
  ISQLQueryConsole,
} from '@/types';
import {
  TabOpened,
  ConsoleType,
  ConsoleStatus,
  DatabaseTypeCode,
  consoleTopComment,
} from '@/utils/constants';
import { DatabaseContext } from '@/context/database';
import styles from './index.less';

interface IProps {
  className?: string;
  windowListChange: (value: IConsole[]) => void;
}

export default memo<IProps>(function ConsoleList(props) {
  const { windowListChange } = props;
  const { consoleId } = qs<{ consoleId: string }>();
  const { model, setCreateConsoleDialog, setDblclickNodeData } = useContext(DatabaseContext);
  const [windowList, setWindowList] = useState<IConsole[]>([]);
  const [activeKey, setActiveKey] = useState<string>(consoleId);
  const { dblclickNodeData, createConsoleDialog } = model;

  useEffect(() => {
    windowListChange(windowList);
  }, [windowList]);

  useEffect(() => {
    if (dblclickNodeData) {
      let flag = false;
      windowList.map((i) => {
        if (i.databaseName === dblclickNodeData.databaseName && i.dataSourceId === dblclickNodeData.dataSourceId) {
          flag = true;
          setActiveKey(i.key);
        }
      });
      const name = [dblclickNodeData?.databaseName,dblclickNodeData.schemaName,'console'].filter(t=>t).join('-')
      if (!flag) {
        let p = {
          name: name,
          type: dblclickNodeData.dataType as DatabaseTypeCode,
          dataSourceId: dblclickNodeData.dataSourceId!,
          databaseName: dblclickNodeData?.databaseName!,
          schemaName: dblclickNodeData.schemaName!,
          status: ConsoleStatus.DRAFT,
          ddl: `${consoleTopComment}`,
          tabOpened: TabOpened.IS_OPEN,
        };

        historyService.saveWindowTab(p).then((res) => {
          const newConsole: IConsole = {
            name: name,
            key: res.toString(),
            type: ConsoleType.SQLQ,
            DBType: dblclickNodeData.dataType! as DatabaseTypeCode,
            databaseName: dblclickNodeData.databaseName!,
            dataSourceId: dblclickNodeData.dataSourceId!,
            dataSourceName: dblclickNodeData.dataSourceName!,
            schemaName: dblclickNodeData.schemaName!,
            consoleId: res,
            ddl: `${consoleTopComment}`,
          };
          setActiveKey(newConsole.key);
          setWindowList([...windowList, newConsole]);
          console.log([...windowList, newConsole])
        });
      }
    }
  }, [dblclickNodeData]);

  useEffect(() => {
    if (createConsoleDialog) {
      createConsole();
    }
  }, [createConsoleDialog]);

  function createConsole() {
    if (!createConsoleDialog) {
      return;
    }
    const name = [createConsoleDialog?.databaseName,createConsoleDialog.schemaName,'console'].filter(t=>t).join('-')
    let p = {
      name: name,
      type: createConsoleDialog.databaseType,
      dataSourceId: createConsoleDialog.dataSourceId,
      databaseName: createConsoleDialog?.databaseName,
      schemaName: createConsoleDialog.schemaName!,
      status: ConsoleStatus.DRAFT,
      ddl: `${consoleTopComment}`,
      tabOpened: TabOpened.IS_OPEN,
    };

    historyService.saveWindowTab(p).then((res) => {
      const newConsole: IConsole = {
        name: name,
        key: res.toString(),
        type: ConsoleType.SQLQ,
        DBType: createConsoleDialog.databaseType,
        databaseName: createConsoleDialog.databaseName,
        dataSourceId: createConsoleDialog.dataSourceId,
        dataSourceName: createConsoleDialog.dataSourceName,
        schemaName: createConsoleDialog.schemaName!,
        consoleId: res,
        ddl: `${consoleTopComment}`,
      };
      setActiveKey(newConsole.key);
      setWindowList([...windowList, newConsole]);
    });
  }

  useEffect(() => {
    getConsoleList();
  }, []);

  function getConsoleList() {
    let p = {
      pageNo: 1,
      pageSize: 999,
      tabOpened: TabOpened.IS_OPEN,
    };

    historyService.getSaveList(p).then((res) => {
      let flag = false;

      const newWindowList: any = [];
      res.data?.map((item, index) => {
        if (item.connectable) {
          newWindowList.push({
            consoleId: item.id,
            ddl: item.ddl,
            name: item.name,
            key: item.id + '',
            DBType: item.type,
            type: ConsoleType.SQLQ,
            status: item.status,
            databaseName: item.databaseName,
            dataSourceName: item?.dataSourceName,
            dataSourceId: item.dataSourceId,
          });
        }
      });

      newWindowList.map((item: any, index: number) => {
        if (!consoleId && index === 0) {
          setActiveKey(item.key);
        } else if (+item.key === +consoleId) {
          flag = true;
          setActiveKey(item.key);
        }
      });
      if (!flag && consoleId) {
        historyService.getWindowTab({ id: consoleId }).then((res: any) => {
          if (res.connectable) {
            newWindowList.push({
              consoleId: res.id,
              ddl: res.ddl,
              name: res.name,
              key: res.id + '',
              DBType: res.type,
              type: ConsoleType.SQLQ,
              status: res.status,
              databaseName: res.databaseName,
              dataSourceName: res?.dataSourceName,
              dataSourceId: res.dataSourceId,
            });
            setActiveKey(res.id + '');
            setWindowList(newWindowList);
          }
        });
      } else {
        setWindowList(newWindowList);
      }
    });
  }

  function renderCurrentTab(i: IConsole) {
    if (i.type === 'editTable') {
      return <ModifyTable data={i as IEditTableConsole}></ModifyTable>;
    } else {
      return (
        <DatabaseQuery
          windowTab={i as ISQLQueryConsole}
          key={i.key}
          activeTabKey={activeKey!}
        />
      );
    }
  }

  const onChangeTab = (newActiveKey: string) => {
    setActiveKey(newActiveKey);
  };

  const onEdit = (targetKey: any, action: 'add' | 'remove', window: any) => {
    // if (action === 'add') {
    //   setCreateConsoleDialog(true)
    // } else {
    //   closeWindowTab(targetKey);
    // }
    if (action === 'remove') {
      closeWindowTab(targetKey, window);
    }
  };

  const closeWindowTab = (targetKey: string, window: any) => {
    let newActiveKey = activeKey;
    let lastIndex = -1;
    windowList.forEach((item, i) => {
      if (item.key === targetKey) {
        lastIndex = i - 1;
      }
    });
    const newPanes = windowList.filter((item) => item.key !== targetKey);
    if (newPanes.length && newActiveKey === targetKey) {
      if (lastIndex >= 0) {
        newActiveKey = newPanes[lastIndex].key;
      } else {
        newActiveKey = newPanes[0].key;
      }
    }
    setWindowList(newPanes);
    setActiveKey(newActiveKey);
    let p: any = {
      id: targetKey,
      tabOpened: 'n',
    };

    if (window.status === 'DRAFT') {
      historyService.deleteWindowTab({ id: window.id });
    } else {
      historyService.updateWindowTab(p);
    }
  };

  return (
    <div className={styles.box}>
      <AppHeader className={styles.appHeader} showRight={false}>
        <div className={styles.tabsBox}>
          {!!windowList.length && (
            <Tabs
              hideAdd
              type="editable-card"
              onChange={onChangeTab}
              activeKey={activeKey}
              onEdit={(targetKey: any, action: 'add' | 'remove') => {
                onEdit(targetKey, action, window);
              }}
              items={windowList.map((t) => {
                return {
                  key: t.key,
                  label: t.name,
                };
              })}
            ></Tabs>
          )}
        </div>
      </AppHeader>
      <div className={styles.databaseQueryBox}>
        {!windowList.length && <div className={styles.ears}>Chat2DB</div>}
        {windowList?.map((i: IConsole, index: number) => {
          return (
            <div
              key={index}
              className={classnames(styles.windowContent, {
                [styles.concealTab]: activeKey !== i.key,
              })}
            >
              {renderCurrentTab(i)}
            </div>
          );
        })}
      </div>
    </div>
  );
});
