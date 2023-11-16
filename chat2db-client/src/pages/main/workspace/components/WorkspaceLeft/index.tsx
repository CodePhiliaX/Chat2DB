import React, { memo, useEffect } from 'react';
import classnames from 'classnames';
import styles from './index.less';
import NewTableList from '../NewTableList';
import WorkspaceLeftHeader from '../WorkspaceLeftHeader';
import useCreateDatabase from '@/components/CreateDatabase';
import { setOpenCreateDatabaseModal } from '@/store/workspace/modal';

const WorkspaceLeft = memo(() => {
  const { createDatabaseDom, openCreateDatabaseModal } = useCreateDatabase();

  useEffect(() => {
    setOpenCreateDatabaseModal(openCreateDatabaseModal);
  }, [openCreateDatabaseModal]);

  return (
    <>
      <div className={classnames(styles.workspaceLeft)}>
        <WorkspaceLeftHeader />
        <NewTableList />
      </div>
      {createDatabaseDom}
    </>
  );
});

export default WorkspaceLeft;
