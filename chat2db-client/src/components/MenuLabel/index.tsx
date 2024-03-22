import React, { memo } from 'react';
import classnames from 'classnames';
import Iconfont from '@/components/Iconfont';
import styles from './index.less';

interface IProps {
  className?: string;
  icon?: string;
  iconBright?: boolean;
  label: string;
}

export default memo<IProps>((props) => {
  const { className, icon, label, iconBright } = props;
  return (
    <div className={classnames(styles.menuLabel, className)}>
      <div className={styles.menuLabelIconBox}>
        {icon && (
          <Iconfont
            className={classnames(styles.menuLabelIcon, { [styles.menuLabelIconBright]: iconBright })}
            code={icon}
          />
        )}
      </div>
      <div className={styles.menuLabelTitle}>{label}</div>
    </div>
  );
});
