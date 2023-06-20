import React, { memo } from 'react';
import styles from './index.less';
import classnames from 'classnames';

interface IProps {
  className?: string;
}

export default memo<IProps>(function XXXXX({ className }) {
  return <div className={classnames(className, styles.box)}>我是组件</div>
})
