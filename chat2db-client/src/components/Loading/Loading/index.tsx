import React, { memo } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import './index.less'

interface IProps {
  className?: any;
}

// TODO： 首屏以及懒加载Loading效果
export default memo(function PageLoading(props: IProps) {
  const { className } = props;
  return <div className={classnames('loading-components-box', className)}>
    <div className="load-container">
      <div className="container container-1">
        <div className="dot dot-1"></div>
        <div className="dot dot-2"></div>
        <div className="dot dot-3"></div>
        <div className="dot dot-4"></div>
      </div>
      <div className="container container-2">
        <div className="dot dot-1"></div>
        <div className="dot dot-2"></div>
        <div className="dot dot-3"></div>
        <div className="dot dot-4"></div>
      </div>
      <div className="container container-3">
        <div className="dot dot-1"></div>
        <div className="dot dot-2"></div>
        <div className="dot dot-3"></div>
        <div className="dot dot-4"></div>
      </div>
    </div>
  </div>

});
