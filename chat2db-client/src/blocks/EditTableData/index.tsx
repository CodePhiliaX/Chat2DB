import React, { memo } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { DatabaseTypeCode } from '@/constants';

interface IProps {
  className?: string;
  dataSourceId: number;
  databaseName: string;
  schemaName: string | undefined;
  tableName?: string;
  databaseType: DatabaseTypeCode;
}

export default memo<IProps>((props) => {
  const { className } = props;
  return <div className={classnames(styles.editTableData, className)}>EditTableData</div>;
});
