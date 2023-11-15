import { ITreeNode } from '@/typings';
import { OperationColumn, WorkspaceTabType } from '@/constants';
import i18n from '@/i18n';
import { v4 as uuid } from 'uuid';

// ----- components -----
import { dataSourceFormConfigs } from '@/components/ConnectionEdit/config/dataSource';
import { IConnectionConfig } from '@/components/ConnectionEdit/config/types';

// ----- config -----
import { ITreeConfigItem, treeConfig } from '../treeConfig';
import { useMemo } from 'react';

// ----- store -----
import { createConsole, addWorkspaceTab } from '@/store/console';

interface IProps {
  treeNodeData: ITreeNode;
  loadData: any;
}

interface IOperationColumnConfigItem {
  text: string;
  icon: string;
  handle: () => void;
}

export const useGetRightClickMenu = (props: IProps) => {
  const { treeNodeData, loadData } = props;

  const rightClickMenu = useMemo(() => {
    // 拿出当前节点的配置
    const treeNodeConfig: ITreeConfigItem = treeConfig[treeNodeData.treeNodeType];
    const { operationColumn } = treeNodeConfig;

    const dataSourceFormConfig = dataSourceFormConfigs.find((t: IConnectionConfig) => {
      return t.type === treeNodeData.extraParams?.databaseType;
    })!;

    // 有些数据库不支持的操作，需要排除掉
    function excludeSomeOperation() {
      const excludes = dataSourceFormConfig.baseInfo.excludes;
      const newOperationColumn: OperationColumn[] = [];
      operationColumn?.map((item: OperationColumn) => {
        let flag = false;
        excludes?.map((t) => {
          if (item === t) {
            flag = true;
          }
        });
        if (!flag) {
          newOperationColumn.push(item);
        }
      });
      return newOperationColumn;
    }

    const operationColumnConfig: { [key in string]: IOperationColumnConfigItem } = {
      // 刷新
      [OperationColumn.Refresh]: {
        text: i18n('common.button.refresh'),
        icon: '\uec08',
        handle: () => {
          loadData({
            refresh: true,
          });
        },
      },
      // 创建console
      [OperationColumn.CreateConsole]: {
        text: i18n('workspace.menu.queryConsole'),
        icon: '\ue619',
        handle: () => {
          createConsole({
            name: 'create console',
            dataSourceId: treeNodeData.extraParams!.dataSourceId!,
            type: treeNodeData.extraParams!.databaseType!,
            databaseName: treeNodeData.extraParams?.databaseName,
            schemaName: treeNodeData.extraParams?.schemaName,
          });
        },
      },
      // 创建表
      [OperationColumn.CreateTable]: {
        text: i18n('editTable.button.createTable'),
        icon: '\ue792',
        handle: () => {
          addWorkspaceTab({
            id: uuid(),
            title: i18n('editTable.button.createTable'),
            type: WorkspaceTabType.CreateTable,
            uniqueData: {
              dataSourceId: treeNodeData.extraParams!.dataSourceId!,
              databaseType: treeNodeData.extraParams!.databaseType!,
              databaseName: treeNodeData.extraParams?.databaseName,
              schemaName: treeNodeData.extraParams?.schemaName,
            },
          });
        },
      },
      // 删除表
      [OperationColumn.DeleteTable]: {
        text: i18n('workspace.menu.deleteTable'),
        icon: '\ue6a7',
        handle: () => {
          // setVerifyDialog(true);
        },
      },
      // 查看ddl
      [OperationColumn.ViewDDL]: {
        text: i18n('workspace.menu.ViewDDL'),
        icon: '\ue665',
        handle: () => {
          //
        },
      },
      // 置顶
      [OperationColumn.Top]: {
        text: treeNodeData.pinned ? i18n('workspace.menu.unPin') : i18n('workspace.menu.pin'),
        icon: treeNodeData.pinned ? '\ue61d' : '\ue627',
        handle: () => {},
      },
      // 编辑表
      [OperationColumn.EditTable]: {
        text: i18n('workspace.menu.editTable'),
        icon: '\ue602',
        handle: () => {},
      },
      // 复制名称
      [OperationColumn.CopyName]: {
        text: i18n('common.button.copyName'),
        icon: '\uec7a',
        handle: () => {
          navigator.clipboard.writeText(treeNodeData.name);
        },
      },
    };

    return excludeSomeOperation().map((t, i) => {
      const concrete = operationColumnConfig[t];
      return {
        key: i,
        onClick: concrete?.handle,
        labelProps: {
          icon: concrete?.icon,
          label: concrete?.text,
        },
      };
    });
  }, [treeNodeData]);

  return rightClickMenu;
};
