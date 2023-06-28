import React, { memo, useRef, useEffect, useState } from 'react';
import { connect } from 'umi'
import styles from './index.less';
import classnames from 'classnames';
import { ConsoleOpenedStatus, ConsoleStatus, consoleTopComment, DatabaseTypeCode } from '@/constants';
import { IConsole, ICreateConsole } from '@/typings';
import historyService from '@/service/history';
import Tabs from '@/components/Tabs';
import LoadingContent from '@/components/Loading/LoadingContent';
import WorkspaceRightItem from '../WorkspaceRightItem';

import { IWorkspaceModelType } from '@/models/workspace';

interface IProps {
  className?: string;
  workspaceModel: IWorkspaceModelType['state'];
  dispatch: any;
}

const WorkspaceRight = memo<IProps>(function (props) {
  const { className } = props;
  const [consoleList, setConsoleList] = useState<IConsole[]>();
  const [activeConsoleId, setActiveConsoleId] = useState<number>();
  const { workspaceModel, dispatch } = props;
  const { databaseAndSchema, curWorkspaceParams, doubleClickTreeNodeData } = workspaceModel;

  useEffect(() => {
    getConsoleList();
  }, [curWorkspaceParams]);

  useEffect(() => {
    // 这里只处理没有console的情况下
    if (!doubleClickTreeNodeData || consoleList?.length) {
      return
    }

    const { extraParams } = doubleClickTreeNodeData;
    const { databaseName, schemaName, dataSourceId, dataSourceName, databaseType, tableName } = extraParams || {};
    const ddl = `SELECT * FROM ${tableName};`;
    const name = [databaseName, schemaName, 'console'].filter((t) => t).join('-');
    let p: any = {
      name: name,
      type: databaseType!,
      dataSourceId: dataSourceId!,
      databaseName: databaseName,
      schemaName: schemaName,
      dataSourceName: dataSourceName!,
      status: ConsoleStatus.DRAFT,
      ddl,
      tabOpened: ConsoleOpenedStatus.IS_OPEN,
    };
    addConsole(p);
    dispatch({
      type: 'workspace/setDoubleClickTreeNodeData',
      payload: ''
    });
  }, [doubleClickTreeNodeData]);

  useEffect(() => {
    if (!consoleList?.length) {
      setActiveConsoleId(undefined)
    }
  }, [consoleList])

  function getConsoleList() {
    let p: any = {
      pageNo: 1,
      pageSize: 999,
      tabOpened: ConsoleOpenedStatus.IS_OPEN,
      ...curWorkspaceParams,
    };

    historyService.getSaveList(p).then((res) => {
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
        console.log(!activeConsoleId && index === 0)
        console.log(activeConsoleId)
        if (!activeConsoleId && index === 0) {
          setActiveConsoleId(item.id);
        } else if (item.id === activeConsoleId) {
          setActiveConsoleId(item.id);
        }
      });
      setConsoleList(newWindowList);
    });
  }

  function onChange(key: number | string) {
    setActiveConsoleId(+key);
  }

  const onEdit = (action: 'add' | 'remove', key?: number) => {
    if (action === 'remove') {
      closeWindowTab(key!);
    }
    if (action === 'add') {
      addConsole();
    }
  };

  const addConsole = (params?: ICreateConsole) => {
    const { dataSourceId, databaseName, schemaName, databaseType } = curWorkspaceParams
    let p = {
      name: `new console${consoleList?.length}`,
      ddl: consoleTopComment,
      dataSourceId: dataSourceId!,
      databaseName: databaseName!,
      schemaName: schemaName!,
      type: databaseType,
      status: ConsoleStatus.DRAFT,
      tabOpened: ConsoleOpenedStatus.IS_OPEN,
    }
    historyService.saveConsole(params || p).then(res => {

      getConsoleList();
    })
  }

  const closeWindowTab = (key: number) => {
    let newActiveKey = activeConsoleId;
    let lastIndex = -1;
    consoleList?.forEach((item, i) => {
      if (item.id === key) {
        lastIndex = i - 1;
      }
    });

    const newPanes = consoleList?.filter((item) => item.id !== key) || [];
    if (newPanes.length && newActiveKey === key) {
      if (lastIndex >= 0) {
        newActiveKey = newPanes[lastIndex].id;
      } else {
        newActiveKey = newPanes[0].id;
      }
    }
    setConsoleList(newPanes);
    setActiveConsoleId(newActiveKey);

    let p: any = {
      id: key,
      tabOpened: 'n',
    };

    const window = consoleList?.find((t) => t.id === key);
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
            onChange={onChange}
            onEdit={onEdit}
            tabs={(consoleList || [])?.map((t, i) => {
              return {
                label: t.name,
                value: t.id,
              };
            })}
          />
        </div>
        {consoleList?.map((t, index) => {
          return <div key={t.id} className={classnames(styles.consoleBox, { [styles.activeConsoleBox]: activeConsoleId === t.id })}>
            <WorkspaceRightItem
              isActive={activeConsoleId === t.id}
              data={
                {
                  initDDL: t.ddl,
                  databaseName: curWorkspaceParams.databaseName!,
                  dataSourceId: curWorkspaceParams.dataSourceId!,
                  type: curWorkspaceParams.databaseType!,
                  schemaName: curWorkspaceParams?.schemaName!,
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
})

const dvaModel = connect(({ workspace }: { workspace: IWorkspaceModelType }) => ({
  workspaceModel: workspace
}))

export default dvaModel(WorkspaceRight) 
