import React, { memo, useState, useMemo } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { useCommonStore } from '@/store/common';
import Iconfont from '@/components/Iconfont';
import BrandLogo from '@/components/BrandLogo';

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

  const isMac = useMemo(() => {
    return window.electronApi?.getPlatform().isMac;
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
        <div className={classnames({ [styles.windowsSpacer]: (!isMac && isMac !== void 0) }, styles.leftSlot)}>
          {(!isMac && isMac !== void 0) && <BrandLogo size={20} className={styles.brandLogo} />}
        </div>
        <div className={styles.appName}>Chat2DB Community</div>
        <div className={styles.rightSlot}>{appTitleBarRightComponent}</div>
      </div>
      {(!isMac && isMac !== void 0) && (
        <div className={styles.windowsCloseBar}>
          <div className={styles.windowsCloseBarItem} onClick={handelMinimizeWindow}>
            <Iconfont size={16} code="&#xe671;" />
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
