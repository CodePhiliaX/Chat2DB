import React, { memo, useEffect, useState, useMemo, Fragment, useRef } from 'react';
import i18n from '@/i18n';
import classnames from 'classnames';

// ----- constants -----
import { WorkspaceTabType, workspaceTabConfig } from '@/constants';
// import WorkspaceExtend from '../WorkspaceExtend';

// ----- components -----
import Tabs, { ITabItem } from '@/components/Tabs';

// ----- hooks -----
import useGetConsoleList from '@/hooks/useGetConsoleList';

// ---- store ----- 
import { useConsoleStore } from '@/store/console';
import { State } from '@/components/StateIndicator';

const WorkspaceTabs = memo(() => {
  // 获取console
  const { consoleList } = useGetConsoleList();

  const {  activeConsoleId } = useConsoleStore(state=> {
    return {
      activeConsoleId: state.activeConsoleId
    }
  });

  const workspaceTabItems: ITabItem[] = useMemo(() => {
    return (
      consoleList?.map((item) => {
        return {
          prefixIcon: workspaceTabConfig[item.type]?.icon,
          label: item.name,
          key: item.id,
          // editableName: item.type === WorkspaceTabType.CONSOLE,
        };
      }) || []
    );
  }, [consoleList]);

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

  function onTabChange(key: string | number | null) {
    setActiveConsoleId(key);
  }

  return (
    <Tabs
      onChange={onTabChange}
      onEdit={onEdit as any}
      activeKey={activeConsoleId}
      editableNameOnBlur={editableNameOnBlur}
      items={workspaceTabItems}
    />
  );
});

export default WorkspaceTabs;
