import React, { memo } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import WorkspaceExtend from '../WorkspaceExtend';

// ----- components -----
import WorkspaceTabs from '../WorkspaceTabs';

const WorkspaceRight = memo(() => {
  return (
    <div className={classnames(styles.workspaceRight)}>
      <WorkspaceTabs />
      <WorkspaceExtend className={styles.workspaceExtend} />
    </div>
  );
});

export default WorkspaceRight;
