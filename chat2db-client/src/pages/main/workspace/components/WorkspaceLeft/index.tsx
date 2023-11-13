import React, { memo } from 'react';
import classnames from 'classnames';
import styles from './index.less';
import NewTableList from '../NewTableList';
import WorkspaceLeftHeader from '../WorkspaceLeftHeader';

const WorkspaceLeft = memo(() => {

  return (
    <div className={classnames(styles.workspaceLeft)}>
      <WorkspaceLeftHeader />
      <div style={{margin: '10px 4px  '}}>这里加分割线还有操作按键？</div>
      <NewTableList />
    </div>
  );
});

export default WorkspaceLeft;
