import React, { memo } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import CustomLayout from '@/components/CustomLayout';

interface IProps {
  className?: string;
}

export default memo<IProps>((props) => {
  const { className } = props;

  const handleDoubleClick = () => {
    window.electronApi?.setMaximize();
  };

  return (
    <div className={classnames(styles.appTitleBar, className)} onDoubleClick={handleDoubleClick}>
      <div />
      <div className={styles.appName}>Chat2DB Community</div>
      <div>
        <CustomLayout />
      </div>
    </div>
  );
});
