import React, { memo, useEffect, useState, useRef } from 'react';
import styles from './index.less';
import classnames from 'classnames';

import { useWorkspaceStore } from '@/pages/main/workspace/store';

// ----- components -----
import OperationLine from '../OperationLine';

import Tree from '@/blocks/Tree';
import { treeConfig } from '@/blocks/Tree/treeConfig';
import { ITreeNode } from '@/typings';
import { TreeNodeType } from '@/constants';

interface IProps {
  className?: string;
}

export default memo<IProps>((props) => {
  const { className } = props;
  const [treeData, setTreeData] = useState<ITreeNode[] | null>(null);

  const [searchValue, setSearchValue] = useState<string>('');

  const currentConnectionDetails = useWorkspaceStore((state) => state.currentConnectionDetails);

  const abortControllerRef = useRef<AbortController | null>(null);

  const getTreeData = (refresh = false) => {
    console.log('[Chat2DB][TableList.getTreeData] called', {
      refresh,
      dataSourceId: currentConnectionDetails?.id,
      dataSourceName: currentConnectionDetails?.alias,
      supportDatabase: currentConnectionDetails?.supportDatabase,
    });

    if (!currentConnectionDetails?.id) {
      setTreeData([]);
      return;
    }

    if (abortControllerRef.current) {
      abortControllerRef.current.abort();
    }
    abortControllerRef.current = new AbortController();
    const signal = abortControllerRef.current.signal;

    const treeNodeType = currentConnectionDetails.supportDatabase ? TreeNodeType.DATA_SOURCE : TreeNodeType.DATABASE;
    setTreeData(null);
    treeConfig[treeNodeType]
      .getChildren?.({
        dataSourceId: currentConnectionDetails.id,
        dataSourceName: currentConnectionDetails.alias,
        refresh: refresh,
        extraParams: {
          dataSourceId: currentConnectionDetails.id,
          dataSourceName: currentConnectionDetails.alias,
          databaseType: currentConnectionDetails.type,
        },
      }, { signal })
      .then((res) => {
        if (signal.aborted) return;
        console.log('[Chat2DB][TableList.getTreeData] success', {
          refresh,
          resultLength: Array.isArray(res) ? res.length : undefined,
        });
        setTreeData(res);
      })
      .catch(() => {
        if (signal.aborted) return;
        setTreeData([]);
      });
  };

  useEffect(() => {
    getTreeData();
  }, [currentConnectionDetails]);

  useEffect(() => {
    return () => {
      if (abortControllerRef.current) {
        abortControllerRef.current.abort();
      }
    };
  }, []);

  return (
    <div className={classnames(styles.treeContainer, className)}>
      <OperationLine getTreeData={getTreeData} searchValue={searchValue} setSearchValue={setSearchValue} />
      <Tree className={styles.treeBox} searchValue={searchValue} treeData={treeData} refreshRootData={getTreeData} />
    </div>
  );
});
