import React, { memo, useEffect, useState } from 'react';
import styles from './index.less';
import classnames from 'classnames';

import { useWorkspaceStore } from '@/store/workspace';

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
  const treeBoxRef = React.useRef<HTMLDivElement>(null);
  const leftModuleTitleRef = React.useRef<HTMLDivElement>(null);

  const [searchValue, setSearchValue] = useState<string>('');


  const { currentConnectionDetails } = useWorkspaceStore((state) => {
    return {
      currentConnectionDetails: state.currentConnectionDetails,
    };
  });

  // 监听treeBox滚动时，给leftModuleTitle添加下阴影
  useEffect(() => {
    const treeBox = treeBoxRef.current;
    const leftModuleTitleDom = leftModuleTitleRef.current;
    if (!treeBox || !leftModuleTitleDom) {
      return;
    }
    const handleScroll = () => {
      const scrollTop = treeBox.scrollTop;
      if (scrollTop > 0) {
        leftModuleTitleDom.classList.add(styles.leftModuleTitleShadow);
      } else {
        leftModuleTitleDom.classList.remove(styles.leftModuleTitleShadow);
      }
    };
    treeBox.addEventListener('scroll', handleScroll);
    return () => {
      treeBox.removeEventListener('scroll', handleScroll);
    };
  }, [treeBoxRef.current, leftModuleTitleRef.current]);

  const getTreeData = (refresh = false) => {
    if (!currentConnectionDetails?.id) {
      return;
    }
    setTreeData(null);
    treeConfig['dataSource']
      .getChildren?.({
        dataSourceId: currentConnectionDetails.id,
        dataSourceName: currentConnectionDetails.name,
        refresh: refresh,
        extraParams: {
          dataSourceId: currentConnectionDetails.id,
          dataSourceName: currentConnectionDetails.name,
          databaseType: currentConnectionDetails.type,
        },
      })
      .then((res) => {
        setTreeData(res);
      });
  }

  useEffect(() => {
    getTreeData();
  }, [currentConnectionDetails]);

  return (
    <div className={classnames(styles.treeContainer, className)}>
      <div ref={leftModuleTitleRef}>
       <OperationLine getTreeData={getTreeData} searchValue={searchValue} setSearchValue={setSearchValue}  />
      </div>
      <Tree className={styles.treeBox} searchValue={searchValue} ref={treeBoxRef} initialData={treeData} />
    </div>
  );
});
