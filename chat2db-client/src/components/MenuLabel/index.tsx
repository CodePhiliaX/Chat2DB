import React, { memo } from 'react';
import classnames from 'classnames';
import Iconfont from '@/components/Iconfont';
import styles from './index.less';

interface IProps {
  className?: string;
  icon?: string;
  label: string;
}

export default memo<IProps>((props) => {
  const { className, icon, label } = props;
  return <div className={classnames(styles.menuLabel, className)}>
    <div className={styles.menuLabelIconBox}>
      {icon && <Iconfont className={styles.menuLabelIcon} code={icon} />}
    </div>
    <div className={styles.menuLabelTitle}>{label}</div>
  </div>;
});
