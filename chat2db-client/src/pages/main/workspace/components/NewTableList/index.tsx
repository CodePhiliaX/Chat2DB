import React, { memo, useEffect, useState } from 'react';
import styles from './index.less';
import classnames from 'classnames';

import { useWorkspaceStore } from '@/store/workspace';  

// ----- components -----
import Iconfont from '@/components/Iconfont';
 
import Tree from '@/blocks/Tree';
import { treeConfig } from '@/blocks/Tree/treeConfig';
import { ITreeNode } from '@/typings';

interface IProps {
  className?: string;
}

export default memo<IProps>((props) => {
  const { className } = props;
  const [treeData, setTreeData] = useState<ITreeNode[] | null>(null);
  const { currentConnectionDetails } = useWorkspaceStore((state) => {
    return {
      currentConnectionDetails: state.currentConnectionDetails,
    }
    });

  useEffect(() => {
    if(!currentConnectionDetails?.id){
      return
    }
    treeConfig['dataSource'].getChildren?.({
      dataSourceId: currentConnectionDetails.id,
      dataSourceName: currentConnectionDetails.name,
      extraParams: {
        dataSourceId: currentConnectionDetails.id,
        dataSourceName: currentConnectionDetails.name,
        databaseType: currentConnectionDetails.type,
      }
    }).then((res) => {
      setTreeData(res)
    });
  }, [currentConnectionDetails]);

  return <div className={classnames(styles.treeContainer, className)}>
    <div className={styles.operationColumn}>
      <Iconfont code="" />
    </div>
    <Tree initialData={treeData} />
  </div>
});
