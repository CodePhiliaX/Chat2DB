import React, { memo, useRef } from 'react';
import styles from './index.less';
// import classnames from 'classnames';
import WorkspaceExtendBody from '../WorkspaceExtend/WorkspaceExtendBody';
import WorkspaceExtendNav from '../WorkspaceExtend/WorkspaceExtendNav';
import { useWorkspaceStore } from '@/pages/main/workspace/store';

// ----- components -----
import WorkspaceTabs from '../WorkspaceTabs';
import DraggableContainer from '@/components/DraggableContainer';

const WorkspaceRight = memo(() => {
  const draggableRef = useRef<any>();
  const { currentWorkspaceExtend } = useWorkspaceStore((state) => {
    return {
      currentWorkspaceExtend: state.currentWorkspaceExtend,
    };
  });
  return (
    <div className={styles.workspaceRight}>
      <DraggableContainer showLine={!!currentWorkspaceExtend} min={200} className={styles.draggableContainer}>
        <WorkspaceTabs />
        <div ref={draggableRef} className={styles.workspaceExtendBody} style={{display: currentWorkspaceExtend ? 'block' : 'none'}}>
          <WorkspaceExtendBody />
        </div>
      </DraggableContainer>

      <WorkspaceExtendNav className={styles.workspaceExtendNav} />
    </div>
  );
});

export default WorkspaceRight;
