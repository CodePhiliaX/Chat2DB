import React, { useState } from 'react';
import mysqlService from '@/service/sql';
import { Button, Checkbox, message } from 'antd';
import { openModal } from '@/store/common/components';
import styles from './deleteTable.less';
import i18n from '@/i18n';

export const deleteDatabase = (treeNodeData, loadData, refreshRootData?: (refresh?: boolean) => void) => {
  openModal({
    width: '450px',
    content: (
      <DeleteDatabaseModalContent
        treeNodeData={treeNodeData}
        loadData={loadData}
        refreshRootData={refreshRootData}
      />
    ),
  });
};

export const DeleteDatabaseModalContent = (params: {
  treeNodeData: any;
  loadData: any;
  refreshRootData?: (refresh?: boolean) => void;
}) => {
  const { treeNodeData, loadData, refreshRootData } = params;
  const [userChecked, setUserChecked] = useState<boolean>(false);

  const onOk = () => {
    const p: any = {
      dataSourceId: treeNodeData.extraParams.dataSourceId,
      databaseName: treeNodeData.name,
    };
    console.log('[Chat2DB][deleteDatabase] confirm delete', {
      params: p,
      currentNode: treeNodeData,
      parentNode: treeNodeData.parentNode,
    });
    mysqlService
      .deleteDatabase(p)
      .then(() => {
        console.log('[Chat2DB][deleteDatabase] delete api success', {
          deletedDatabase: treeNodeData.name,
          hasParentNode: !!treeNodeData.parentNode,
          parentNode: treeNodeData.parentNode,
        });
        if (treeNodeData.parentNode) {
          console.log('[Chat2DB][deleteDatabase] refresh parent tree node', {
            parentUuid: treeNodeData.parentNode.uuid,
            parentName: treeNodeData.parentNode.name,
            parentType: treeNodeData.parentNode.treeNodeType,
          });
          loadData({
            refresh: true,
            treeNodeData: treeNodeData.parentNode,
          });
        } else {
          console.log('[Chat2DB][deleteDatabase] refresh root tree because parentNode is empty');
          refreshRootData?.(true);
        }
        openModal(false);
      })
      .catch((error) => {
        console.error('Error deleting database:', error);
        message.error(i18n('workspace.tree.delete.database.error') || 'Failed to delete database');
      });
  };

  return (
    <div className={styles.deleteModalContent}>
      <div className={styles.title}>{i18n('workspace.tree.delete.database.tip', `"${treeNodeData.name}"`)}</div>
      <div className={styles.checkContainer}>
        <Checkbox
          value={userChecked}
          onChange={(e) => {
            setUserChecked(e.target.checked);
          }}
        >
          {i18n('workspace.tree.delete.tip')}
        </Checkbox>
      </div>
      <div className={styles.deleteTableFooter}>
        <Button
          type="primary"
          onClick={() => {
            openModal(false);
          }}
        >
          {i18n('common.button.cancel')}
        </Button>
        <Button disabled={!userChecked} onClick={onOk}>
          {i18n('common.button.affirm')}
        </Button>
      </div>
    </div>
  );
};
