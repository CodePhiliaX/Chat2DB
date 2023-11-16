import React, { memo } from 'react';
import classnames from 'classnames';
import styles from './index.less';
import NewTableList from '../NewTableList';
import WorkspaceLeftHeader from '../WorkspaceLeftHeader';
import Iconfont from '@/components/Iconfont'


const WorkspaceLeft = memo(() => {

  return (
    <div className={classnames(styles.workspaceLeft)}>
      <WorkspaceLeftHeader />
      <NewTableList />
    </div>
  );
});

export default WorkspaceLeft;
