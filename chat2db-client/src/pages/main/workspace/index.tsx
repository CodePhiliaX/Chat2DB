import React, { memo, useEffect, useRef } from 'react';
import classnames from 'classnames';

import { useWorkspaceStore } from '@/pages/main/workspace/store';

import DraggableContainer from '@/components/DraggableContainer';
import WorkspaceLeft from './components/WorkspaceLeft';
import WorkspaceRight from './components/WorkspaceRight';

import useMonacoTheme from '@/components/MonacoEditor/useMonacoTheme';
import shortcutKeyCreateConsole from './functions/shortcutKeyCreateConsole';

import styles from './index.less';

const workspacePage = memo(() => {
  const draggableRef = useRef<any>();
  const { panelLeft, panelLeftWidth } = useWorkspaceStore((state) => {
    return {
      panelLeft: state.layout.panelLeft,
      panelLeftWidth: state.layout.panelLeftWidth,
    };
  });

  // 编辑器的主题
  useMonacoTheme();
  // 快捷键

  useEffect(() => {
    shortcutKeyCreateConsole();
  }, []);

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
        <WorkspaceRight />
      </DraggableContainer>
    </div>
  );
});

export default workspacePage;
