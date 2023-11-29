import React, { memo } from 'react';
import styles from './index.less';
import classnames from 'classnames';

// ----- components -----
import WorkspaceTabs from '../WorkspaceTabs';

const WorkspaceRight = memo(() => {
  return (
    <div className={classnames(styles.workspaceRight)}>
      <WorkspaceTabs />
    </div>
  );
});

export default WorkspaceRight;
