import React, { memo } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { useCommonStore } from '@/store/common';

// import Iconfont from '@/components/Iconfont';

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

  const handleDoubleClick = () => {
    window.electronApi?.setMaximize();
  };

  return (
    <div className={classnames(styles.appTitleBar, className)} onDoubleClick={handleDoubleClick}>
      <div />
      <div className={styles.appName}>Chat2DB Community</div>
      <div>{appTitleBarRightComponent}</div>
      {/* <div className={styles.windowsCloseBar}>
        <div>
          <Iconfont code="icon-minimize" />
        </div>
        <div>
          <Iconfont code="icon-minimize" />
        </div>
        <div>
          <Iconfont code="icon-minimize" />
        </div>
      </div> */}
    </div>
  );
});
