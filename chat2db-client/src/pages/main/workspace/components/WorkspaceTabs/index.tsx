import React, { memo, useEffect, useMemo, Fragment } from 'react';
import styles from './index.less';
import i18n from '@/i18n';
import { Button } from 'antd';

// ----- constants -----
import { WorkspaceTabType, workspaceTabConfig } from '@/constants';
import { IWorkspaceTab } from '@/typings';
// import WorkspaceExtend from '../WorkspaceExtend';

// ----- components -----
import Tabs, { ITabItem } from '@/components/Tabs';
import SearchResult from '@/components/SearchResult';
import DatabaseTableEditor from '@/blocks/DatabaseTableEditor';
import SQLExecute from '../SQLExecute';
import ViewAllTable from '../ViewAllTable';
import Iconfont from '@/components/Iconfont';
import ShortcutKey from '@/components/ShortcutKey';

// ---- store -----
import {
  getSavedConsoleList,
  setActiveConsoleId,
  setWorkspaceTabList,
  createConsole,
} from '@/pages/main/workspace/store/console';
import { useWorkspaceStore } from '@/pages/main/workspace/store';

// ----- services -----
import historyService from '@/service/history';

import indexedDB from '@/indexedDB';

const WorkspaceTabs = memo(() => {
  const { activeConsoleId, consoleList, workspaceTabList } = useWorkspaceStore((state) => {
    return {
      consoleList: state.consoleList,
      activeConsoleId: state.activeConsoleId,
      workspaceTabList: state.workspaceTabList,
    };
  });

  const currentConnectionDetails = useWorkspaceStore((state) => state.currentConnectionDetails);

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
          uniqueData: {
            dataSourceId: item.dataSourceId,
            dataSourceName: item.dataSourceName,
            databaseType: item.type,
            databaseName: item.databaseName,
            schemaName: item.schemaName,
            status: item.status,
            ddl: item.ddl,
          },
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

  const createNewConsole = () => {
    if (currentConnectionDetails) {
      createConsole({
        dataSourceId: currentConnectionDetails.id,
        dataSourceName: currentConnectionDetails.alias,
        databaseType: currentConnectionDetails.type,
      });
    }
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
        if (editData?.type === WorkspaceTabType.CONSOLE) {
          closeWindowTab(item.key as number);
        }
      });
    }
    if (action === 'add') {
      createNewConsole();
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

  // 渲染sql执行器
  const renderSQLExecute = (item: IWorkspaceTab) => {
    const { uniqueData } = item;
    return (
      <SQLExecute
        boundInfo={{
          dataSourceId: uniqueData.dataSourceId,
          dataSourceName: uniqueData.dataSourceName,
          databaseName: uniqueData?.databaseName,
          databaseType: uniqueData.databaseType,
          schemaName: uniqueData?.schemaName,
          consoleId: item.id as number,
          status: uniqueData.status,
        }}
        initDDL={uniqueData.ddl}
        loadSQL={uniqueData.loadSQL}
      />
    );
  };

  // 渲染表格编辑器
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
        submitCallback={uniqueData.submitCallback}
      />
    );
  };

  // 渲染搜索结果
  const renderSearchResult = (item: IWorkspaceTab) => {
    const { uniqueData } = item;
    return <SearchResult sql={uniqueData.sql} executeSqlParams={uniqueData} concealTabHeader />;
  };

  // 渲染所有表
  const renderViewAllTable = (item: IWorkspaceTab) => {
    const { uniqueData } = item;
    return <ViewAllTable />;
  };

  // 根据不同的tab类型渲染不同的内容
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
      case WorkspaceTabType.ViewAllTable:
        return renderViewAllTable(item);
      default:
        return <div>未知类型</div>;
    }
  };

  // tab列表
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

  function renderCreateConsoleButton() {
    return (
      <div className={styles.createButtonBox}>
        <Button
          className={styles.createButton}
          type="primary"
          onClick={() => {
            createNewConsole();
          }}
        >
          <Iconfont code="&#xe63a;" />
          {i18n('common.button.createConsole')}
        </Button>
      </div>
    );
  }

  return workspaceTabItems?.length ? (
    <Tabs
      className={styles.tabBox}
      onChange={onTabChange as any}
      onEdit={handelTabsEdit as any}
      activeKey={activeConsoleId}
      editableNameOnBlur={editableNameOnBlur}
      items={workspaceTabItems}
    />
  ) : (
    <div className={styles.ears}>
      <ShortcutKey slot={renderCreateConsoleButton} />
    </div>
  );
});

export default WorkspaceTabs;
