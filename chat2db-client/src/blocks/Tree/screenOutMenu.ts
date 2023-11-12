import { ITreeNode } from '@/typings';
import { OperationColumn } from '@/constants';
import i18n from '@/i18n';

// ----- components -----
import { dataSourceFormConfigs } from '@/components/ConnectionEdit/config/dataSource';
import { IConnectionConfig } from '@/components/ConnectionEdit/config/types';


// ----- config -----
import { ITreeConfigItem, treeConfig } from './treeConfig';

// 所有的方法
// import { refreshTreeNode } from './functions/refresh'

interface IProps {
  treeNodeData: ITreeNode;
  loadData: any;
}

interface IOperationColumnConfigItem {
  text: string;
  icon: string;
  handle: () => void;
}

export const screenOutMenu = (props: IProps) => {
  const { treeNodeData, loadData } = props;

    // 拿出当前节点的配置
    const treeNodeConfig: ITreeConfigItem = treeConfig[treeNodeData.treeNodeType];
    const { operationColumn } = treeNodeConfig;

  const dataSourceFormConfig = dataSourceFormConfigs.find((t: IConnectionConfig) => {
    return t.type === treeNodeData.extraParams?.databaseType;
  })!;

  const operationColumnConfig: { [key in OperationColumn]: IOperationColumnConfigItem } = {
    [OperationColumn.Refresh]: {
      text: i18n('common.button.refresh'),
      icon: '\uec08',
      handle: loadData,
    },
  };

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

  return excludeSomeOperation().map((t, i) => {
    const concrete = operationColumnConfig[t];
    return {
      key: i,
      onClick: concrete?.handle,
      labelProps: {
        icon:concrete?.icon,
        label: concrete?.text
      }
    }
  });
};
