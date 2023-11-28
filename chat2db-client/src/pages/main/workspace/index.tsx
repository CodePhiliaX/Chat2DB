import React, { memo, useRef } from 'react';
import classnames from 'classnames';

import { useWorkspaceStore } from '@/pages/main/workspace/store';

import DraggableContainer from '@/components/DraggableContainer';
import WorkspaceLeft from './components/WorkspaceLeft';
import NewWorkspaceRight from './components/NewWorkspaceRight';

import useMonacoTheme from '@/components/MonacoEditor/useMonacoTheme';

import styles from './index.less';

const workspacePage = memo(() => {
  const draggableRef = useRef<any>();
  const { panelLeft, panelLeftWidth } = useWorkspaceStore((state) => {
    return {
      panelLeft: state.layout.panelLeft,
      panelLeftWidth: state.layout.panelLeftWidth,
    }
  });

  // 编辑器的主题
  useMonacoTheme();

  return (
    <div className={styles.workspace}>
      <DraggableContainer className={styles.workspaceMain}>
        <div
          ref={draggableRef}
          style={{ '--panel-left-width': `${panelLeftWidth}px` } as any}
          className={classnames({ [styles.hiddenPanelLeft]: !panelLeft }, styles.boxLeft)}
        >
          <WorkspaceLeft />
        </div>
        <NewWorkspaceRight />
      </DraggableContainer>
    </div>
  );
});

export default workspacePage;
