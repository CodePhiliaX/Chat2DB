import React, { memo, useRef } from 'react';
import styles from './index.less';
// import classnames from 'classnames';
import WorkspaceExtendBody from '../WorkspaceExtend/WorkspaceExtendBody';
import WorkspaceExtendNav from '../WorkspaceExtend/WorkspaceExtendNav';

// ----- components -----
import WorkspaceTabs from '../WorkspaceTabs';
import DraggableContainer from '@/components/DraggableContainer';

const WorkspaceRight = memo(() => {
  const draggableRef = useRef<any>();
  return (
    <div className={styles.workspaceRight}>
      <DraggableContainer min={200} className={styles.draggableContainer}>
        <WorkspaceTabs />
        <div ref={draggableRef} className={styles.workspaceExtendBody}>
          <WorkspaceExtendBody />
        </div>
      </DraggableContainer>
      <WorkspaceExtendNav className={styles.workspaceExtendNav} />
    </div>
  );
});

export default WorkspaceRight;
