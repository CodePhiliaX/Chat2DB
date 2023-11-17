import React, { memo } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import i18n from '@/i18n';

interface IProps {
  className?: string;
  description?: string;
  duration?: number;
  dataLength?: number;
}

export default memo<IProps>((props) => {
  const { className, description, duration, dataLength } = props;
  return (
    <div className={classnames(styles.statusBar, className)}>
      <span>{`【${i18n('common.text.result')}】${description}.`}</span>
      <span>{`【${i18n('common.text.timeConsuming')}】${duration}ms.`}</span>
      {!!dataLength && <span>{`【${i18n('common.text.searchRow')}】${dataLength} ${i18n('common.text.row')}.`}</span>}
    </div>
  );
});
