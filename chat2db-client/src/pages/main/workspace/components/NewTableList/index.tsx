import React, { memo, useEffect, useState } from 'react';
import styles from './index.less';
import classnames from 'classnames';

import { useWorkspaceStore } from '@/pages/main/workspace/store';

// ----- components -----
import OperationLine from '../OperationLine';

import Tree from '@/blocks/Tree';
import { treeConfig } from '@/blocks/Tree/treeConfig';
import { ITreeNode } from '@/typings';

interface IProps {
  className?: string;
}

export default memo<IProps>((props) => {
  const { className } = props;
  const [treeData, setTreeData] = useState<ITreeNode[] | null>(null);

  const [searchValue, setSearchValue] = useState<string>('');

  const currentConnectionDetails = useWorkspaceStore((state) => state.currentConnectionDetails);

  const getTreeData = (refresh = false) => {
    if (!currentConnectionDetails?.id) {
      return;
    }
    setTreeData(null);
    treeConfig['dataSource']
      .getChildren?.({
        dataSourceId: currentConnectionDetails.id,
        dataSourceName: currentConnectionDetails.alias,
        refresh: refresh,
        extraParams: {
          dataSourceId: currentConnectionDetails.id,
          dataSourceName: currentConnectionDetails.alias,
          databaseType: currentConnectionDetails.type,
        },
      })
      .then((res) => {
        setTreeData(res);
      })
      .catch(() => {
        setTreeData([]);
      });
  };

  useEffect(() => {
    getTreeData();
  }, [currentConnectionDetails]);

  return (
    <div className={classnames(styles.treeContainer, className)}>
      <OperationLine getTreeData={getTreeData} searchValue={searchValue} setSearchValue={setSearchValue} />
      <Tree className={styles.treeBox} searchValue={searchValue} treeData={treeData} />
    </div>
  );
});
