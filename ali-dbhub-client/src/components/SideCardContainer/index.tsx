import React, { memo } from 'react';
import styles from './index.less';
import classnames from 'classnames';

interface IProps {
  className?: string;
  title: string | React.ReactDOM;
  aux: string | React.ReactDOM;
}

export default memo<IProps>(function SideCardContainer({ className, children, title, aux }) {
  return <div className={classnames(className, styles.box)}>
    <div className={styles.header}>
      <div className={styles.title}>{title}</div>
      <div className={styles.aux}>{aux}</div>
    </div>
    <div className={styles.main}>
      {children}
    </div>
  </div>
})
