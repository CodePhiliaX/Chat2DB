import React, { memo, useEffect, useMemo, Fragment } from 'react';
import styles from './index.less';

// ----- constants -----
import { WorkspaceTabType, workspaceTabConfig } from '@/constants';
import { IWorkspaceTab } from '@/typings';
// import WorkspaceExtend from '../WorkspaceExtend';

// ---- hooks -----
import useCreateConsole from '@/hooks/useCreateConsole';

// ----- components -----
import Tabs, { ITabItem } from '@/components/Tabs';
import SearchResult from '@/components/SearchResult';
import DatabaseTableEditor from '@/blocks/DatabaseTableEditor';
import SQLExecute from '@/blocks/SQLExecute';

// ---- store -----
import { useConsoleStore, getSavedConsoleList, setActiveConsoleId, setWorkspaceTabList } from '@/store/console';
import { useWorkspaceStore } from '@/store/workspace';

// ----- services -----
import historyService from '@/service/history';

import indexedDB from '@/indexedDB';

const WorkspaceTabs = memo(() => {
  const { activeConsoleId, consoleList, workspaceTabList } = useConsoleStore((state) => {
    return {
      consoleList: state.consoleList,
      activeConsoleId: state.activeConsoleId,
      workspaceTabList: state.workspaceTabList,
    };
  });

  const currentConnectionDetails = useWorkspaceStore((state) => state.currentConnectionDetails);

  const { createConsole } = useCreateConsole();

  // 获取console
  useEffect(() => {
    getSavedConsoleList();
  }, []);

  // consoleList 先转换为通用的 workspaceTabList
  useEffect(() => {
    const _workspaceTabItems =
      consoleList?.map((item) => {
        return {
          id: item.id,
          type: item.operationType,
          title: item.name,
          uniqueData: {},
        };
      }) || [];
    setWorkspaceTabList(_workspaceTabItems);
  }, [consoleList]);

  // 关闭tab
  const closeWindowTab = (key: number) => {
    const p: any = {
      id: key,
      tabOpened: 'n',
    };

    historyService.updateSavedConsole(p).then(() => {
      indexedDB.deleteData('chat2db', 'workspaceConsoleDDL', key);
    });
  };

  // 删除 新增tab
  const handelTabsEdit = (action: 'add' | 'remove', data: ITabItem[]) => {
    if (action === 'remove') {
      setWorkspaceTabList(
        workspaceTabList?.filter((t) => {
          return data.findIndex((item) => item.key === t.id) === -1;
        }) || [],
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
      });
    }
    if (action === 'add') {
      createConsole({
        dataSourceId: currentConnectionDetails?.id,
        type: currentConnectionDetails?.type,
      });
    }
  };

  // 切换tab
  const onTabChange = (key: string | null) => {
    setActiveConsoleId(key);
  };

  // 编辑名称
  const editableNameOnBlur = (t: ITabItem) => {
    const _params: any = {
      id: t.key,
      name: t.label,
    };
    historyService.updateSavedConsole(_params);
  };

  // 修改tab详情
  const changeTabDetails = (data: IWorkspaceTab) => {
    const list =
      workspaceTabList?.map((t) => {
        if (t.id === data.id) {
          return data;
        }
        return t;
      }) || [];
    setWorkspaceTabList(list);
  };

  const renderSQLExecute = (item: IWorkspaceTab) => {
    const { uniqueData } = item;
    return (
      <SQLExecute
        boundInfo={{
          dataSourceId: uniqueData.dataSourceId,
          databaseName: uniqueData.databaseName,
          type: uniqueData.databaseType,
          schemaName: uniqueData?.schemaName,
        }}
        initDDL={uniqueData.ddl}
        consoleId={item.id as number}
      />
    );
  };

  const renderTableEditor = (item: IWorkspaceTab) => {
    const { uniqueData } = item;
    return (
      <DatabaseTableEditor
        tabDetails={item}
        changeTabDetails={changeTabDetails}
        dataSourceId={uniqueData.dataSourceId}
        databaseName={uniqueData.databaseName!}
        databaseType={uniqueData?.databaseType}
        schemaName={uniqueData?.schemaName}
        tableName={uniqueData.tableName}
      />
    );
  };

  const renderSearchResult = (item: IWorkspaceTab) => {
    const { uniqueData } = item;
    return <SearchResult sql={uniqueData.sql} executeSqlParams={uniqueData} />;
  };

  const workspaceTabConnectionMap = (item: IWorkspaceTab) => {
    switch (item.type) {
      case WorkspaceTabType.CONSOLE:
      case WorkspaceTabType.FUNCTION:
      case WorkspaceTabType.PROCEDURE:
      case WorkspaceTabType.TRIGGER:
      case WorkspaceTabType.VIEW:
        return renderSQLExecute(item);
      case WorkspaceTabType.EditTable:
      case WorkspaceTabType.CreateTable:
        return renderTableEditor(item);
      case WorkspaceTabType.EditTableData:
        return renderSearchResult(item);
      default:
        return <div>未知类型</div>;
    }
  };

  const workspaceTabItems = useMemo(() => {
    return workspaceTabList?.map((item) => {
      return {
        prefixIcon: workspaceTabConfig[item.type]?.icon,
        label: item.title,
        key: item.id,
        editableName: item.type === WorkspaceTabType.CONSOLE,
        children: <Fragment key={item.id}>{workspaceTabConnectionMap(item)}</Fragment>,
      };
    });
  }, [workspaceTabList]);

  return (
    <Tabs
      className={styles.tabBox}
      onChange={onTabChange as any}
      onEdit={handelTabsEdit as any}
      activeKey={activeConsoleId}
      editableNameOnBlur={editableNameOnBlur}
      items={workspaceTabItems}
    />
  );
});

export default WorkspaceTabs;
