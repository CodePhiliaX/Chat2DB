import React, { memo } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import logo from '@/assets/logo/logo.webp';

interface IProps extends React.DetailedHTMLProps<React.HTMLAttributes<HTMLDivElement>, HTMLDivElement> {
  className?: any;
  size?: number;
}

export default memo<IProps>(({ className, size = 48, ...res }) => {
  return (
    <div {...res} className={classnames(className, styles.box)} style={{ height: `${size}px`, width: `${size}px` }}>
      <img src={logo} alt="" />
    </div>
  );
});
