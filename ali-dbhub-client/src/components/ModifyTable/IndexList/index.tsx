import React, { memo } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import Table from '@/components/Table'


interface IProps {
  className?: string;
}

export default memo<IProps>(function IndexList({ className }) {
  return <div className={classnames(className, styles.box)}>
    {/* <Table></Table> */}
  </div>
})
