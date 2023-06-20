import React, { memo, PropsWithChildren } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import Iconfont from '@/components/Iconfont';
import Setting from '@/components/Setting';

interface IProps {
  className?: any;
  showRight?: boolean;
}

export default memo<PropsWithChildren<IProps>>(function AppHeader(props) {
  const { className, children, showRight = true } = props;

  const refreshPage = () => {
    location.reload();
  };

  return (
    <div className={classnames(className, styles.header)}>
      {/* <div className={styles.headerBox}> */}
      <div className={styles.headerLeft}>
        {children}
      </div>
      {
        showRight &&
        <div className={styles.headerRight}>
          <div className={styles.refreshBox} onClick={refreshPage}>
            <Iconfont code="&#xe62d;" />
          </div>
        </div>
      }
      {/* </div> */}
    </div>
  );
});
