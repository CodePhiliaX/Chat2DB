import React, { memo } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import Loading from '../Loading/Loading';
import Iconfont from '../Iconfont';
import { Spin } from 'antd';

interface IProps {
  className?: string;
  state: 'loading' | 'empty' | 'error' | 'success';
  text?: string;
}

export const enum State {
  LOADING = 'loading',
  EMPTY = 'empty',
  ERROR = 'error',
  SUCCESS = 'success',
}

const config = {
  loading: {
    icon: '\ue6cd;'
  },
  empty: {
    icon: '\ue760'
  },
  error: {
    icon: '\ue755'
  },
  success: {
    icon: '\ue62e'
  },
}

export default memo<IProps>(function StateIndicator({ className, state, text }) {

  const renderState = () => {
    switch (state) {
      case 'loading':
        return <Spin />;
      case 'error':
        return <div className={styles.errorBox}>
          <div className={classnames(className, styles[state])}></div>
          <div className={styles.errorText}>{text}</div>
        </div>
      case 'success':
        return <div className={styles.successBox}>
          <div className={classnames(className, styles[state])}></div>
          <div className={styles.successText}>{text}</div>
        </div>
      default:
        return <div className={classnames(className, styles[state])}></div >
    }
  }
  return <div className={classnames(className, styles.box)}>
    {renderState()}
  </div>

})
