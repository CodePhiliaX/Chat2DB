import React, { memo } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import Loading from '@/components/Loading/Loading'

interface IProps {
  className?: string;
}

export default memo<IProps>(function LazyLoading({ className }) {
  return <div className={classnames(className, styles.box)}>
    <Loading></Loading>
  </div>
})
