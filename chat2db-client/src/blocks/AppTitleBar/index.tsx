import React, { memo, useMemo } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { useCommonStore } from '@/store/common';
import Iconfont from '@/components/Iconfont';

interface IProps {
  className?: string;
}

export default memo<IProps>((props) => {
  const { className } = props;

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
  };

  const handelMaximize = () => {
    window.electronApi?.setMaximize();
  }

  return (
    <div className={classnames(styles.appTitleBar, className)} onDoubleClick={handleDoubleClick}>
      <div className={styles.appTitleBarGlobal}>
        <div className={classnames({ [styles.windowsSpacer]: isWin })} />
        <div className={styles.appName}>Chat2DB Community</div>
        <div>{appTitleBarRightComponent}</div>
      </div>
      {isWin && (
        <div className={styles.windowsCloseBar}>
          <div className={styles.windowsCloseBarItem}>
            <Iconfont code="&#xeb78;" />
          </div>
          <div className={styles.windowsCloseBarItem} onClick={handelMaximize}>
            <Iconfont code="&#xeb78;" />
          </div>
          <div className={styles.windowsCloseBarItem}>
            <Iconfont code="&#xeb78;" />
          </div>
        </div>
      )}
    </div>
  );
});
