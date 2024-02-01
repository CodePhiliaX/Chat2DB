import React, { memo, useMemo } from 'react';
import { useCommonStore } from '@/store/common';
import { IconButton, Logo } from '@chat2db/ui';
import { Minus, Square, X } from 'lucide-react';
import { useStyles } from './style';

interface AppBarProps {
  className?: string;
}

const AppBar = memo<AppBarProps>(({ className }) => {
  const { styles, cx } = useStyles();

  const appBarType = useMemo(() => {
    if (window.electronApi?.getPlatform().isMac === undefined) {
      return 'Web';
    }
    return window.electronApi?.getPlatform().isMac ? 'Mac' : 'Windows';
  }, []);

  const { appTitleBarRightComponent } = useCommonStore((state) => ({
    appTitleBarRightComponent: state.appTitleBarRightComponent,
  }));

  const handleDoubleClick = () => {
    window.electronApi?.setMaximize();
  };

  const handelMinimizeWindow = (e) => {
    e.stopPropagation();
    window.electronApi?.minimizeWindow();
  };

  const handelMaximizeWindow = (e) => {
    e.stopPropagation();
    window.electronApi?.setMaximize();
  };

  const handelCloseWindow = (e) => {
    e.stopPropagation();
    window.electronApi?.closeWindow();
  };

  if (appBarType === 'Web') {
    return <></>;
  }

  return (
    <div className={cx(styles.appBar, className)} onDoubleClick={handleDoubleClick}>
      <div className={styles.logoContainer}>
        <Logo type="imageWithText" />
        <div className={styles.logoRightSolt}>{appTitleBarRightComponent}</div>
      </div>
      {appBarType !== 'Mac' && (
        <div className={styles.windowsActionBar}>
          <IconButton className={styles.windowsAction} icon={Minus} onClick={handelMinimizeWindow} />
          <IconButton className={styles.windowsAction} icon={Square} onClick={handelMaximizeWindow} />
          <IconButton className={styles.windowsAction} icon={X} onClick={handelCloseWindow} />
        </div>
      )}
    </div>
  );
});

export default AppBar;
