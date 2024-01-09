import React, { memo, useState, useMemo } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { useCommonStore } from '@/store/common';
import Iconfont from '@/components/Iconfont';

interface IProps {
  className?: string;
}

export default memo<IProps>((props) => {
  const { className } = props;
  const [isMaximized, setIsMaximize] = useState(window.electronApi?.isMaximized());

  const { appTitleBarRightComponent } = useCommonStore((state) => {
    return {
      appTitleBarRightComponent: state.appTitleBarRightComponent,
    };
  });

  const isWin = useMemo(() => {
    return window.electronApi?.getPlatform().isWin;
  }, []);

  // const isWin = true;

  const handleDoubleClick = () => {
    window.electronApi?.setMaximize();
    setIsMaximize(!isMaximized);
  };

  const handelMinimizeWindow = (e) => {
    e.stopPropagation();
    window.electronApi?.minimizeWindow();
  };

  const handelMaximize = (e) => {
    e.stopPropagation();
    window.electronApi?.setMaximize();
    setIsMaximize(!isMaximized);
  };

  const handelCloseWindow = (e) => {
    e.stopPropagation();
    window.electronApi?.closeWindow();
  };

  return (
    <div className={classnames(styles.appTitleBar, className)} onDoubleClick={handleDoubleClick}>
      <div className={styles.appTitleBarGlobal}>
        <div className={classnames({ [styles.windowsSpacer]: isWin })} />
        <div className={styles.appName}>Chat2DB Community</div>
        <div>{appTitleBarRightComponent}</div>
      </div>
      {isWin && (
        <div className={styles.windowsCloseBar}>
          <div className={styles.windowsCloseBarItem} onClick={handelMinimizeWindow}>
            <Iconfont code="&#xe671;" />
          </div>
          <div className={styles.windowsCloseBarItem} onClick={handelMaximize}>
            {isMaximized ? <Iconfont code="&#xe66e;" /> : <Iconfont code="&#xe66b;" />}
          </div>
          <div className={styles.windowsCloseBarItem} onClick={handelCloseWindow}>
            <Iconfont code="&#xe66f;" />
          </div>
        </div>
      )}
    </div>
  );
});
