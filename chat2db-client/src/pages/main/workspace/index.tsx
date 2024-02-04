import React, { memo, useEffect } from 'react';
import { DraggablePanel } from '@chat2db/ui';

import WorkspaceLeft from './components/WorkspaceLeft';
import WorkspaceRight from './components/WorkspaceRight';

import useMonacoTheme from '@/components/MonacoEditor/useMonacoTheme';
import shortcutKeyCreateConsole from './functions/shortcutKeyCreateConsole';

import styles from './index.less';
import { useStyle } from './style';

const workspacePage = memo(() => {
  const {} = useStyle();

  // 编辑器的主题
  // useMonacoTheme();

  // 快捷键
  useEffect(() => {
    shortcutKeyCreateConsole();
  }, []);

  return (
    <div className={styles.workspace}>
      <DraggablePanel direction={'horizontal'}>
        <WorkspaceLeft />
        <WorkspaceRight />
      </DraggablePanel>
    </div>
  );
});

export default workspacePage;
