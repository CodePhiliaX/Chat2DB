import React, { memo, useEffect, useState, useMemo, Fragment, useRef } from 'react';
import i18n from '@/i18n';
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
