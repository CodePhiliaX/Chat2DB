import React, { memo } from 'react';
import i18n from '@/i18n';
import classnames from 'classnames';
import styles from './index.less';
import TableList from '../TableList';
import WorkspaceLeftHeader from '../WorkspaceLeftHeader';
import CreateDatabase from '@/components/CreateDatabase';
import Iconfont from '@/components/Iconfont';
import { useConnectionStore } from '@/store/connection';
import { useGlobalStore } from '@/store/global';

const WorkspaceLeft = memo(() => {
  const { connectionList } = useConnectionStore((state) => {
    return {
      connectionList: state.connectionList,
    };
  });

  const jumpPage = () => {
    useGlobalStore.getState().setMainPageActiveTab('workspace');
  };

  return (
    <>
      <div className={classnames(styles.workspaceLeft)}>
        {connectionList?.length ? (
          <>
            <WorkspaceLeftHeader />
            <TableList />
          </>
        ) : (
          <div className={styles.noConnectionList}>
            <Iconfont className={styles.noConnectionListIcon} code="&#xe638;" />
            <div className={styles.noConnectionListTips}>{i18n('workspace.tips.noConnection')}</div>
            <div>
              <span className={styles.create} onClick={jumpPage}>
                {i18n('common.title.create')}
              </span>
              {i18n('connection.title.connections')}
            </div>
          </div>
        )}
      </div>
      <CreateDatabase />
    </>
  );
});

export default WorkspaceLeft;
