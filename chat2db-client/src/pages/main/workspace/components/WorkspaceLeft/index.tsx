import React, { memo } from 'react';
import classnames from 'classnames';
import styles from './index.less';
import NewTableList from '../TableList';
import WorkspaceLeftHeader from '../WorkspaceLeftHeader';
import CreateDatabase from '@/components/CreateDatabase';

const WorkspaceLeft = memo(() => {
  return (
    <>
      <div className={classnames(styles.workspaceLeft)}>
        <WorkspaceLeftHeader />
        <NewTableList />
      </div>
      <CreateDatabase />
    </>
  );
});

export default WorkspaceLeft;
