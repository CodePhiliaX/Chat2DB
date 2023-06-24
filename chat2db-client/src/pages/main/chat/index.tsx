import React, { memo } from 'react';
import styles from './index.less';
import classnames from 'classnames';

interface IProps {
  className?: string;
}

export default memo<IProps>(function Chat(props) {
  const { className } = props
  return <div className={classnames(styles.box, className)}>
    Chat
  </div>
})
