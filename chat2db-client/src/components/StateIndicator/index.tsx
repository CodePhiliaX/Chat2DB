import React, { memo } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { Spin } from 'antd';

interface IProps {
  className?: string;
  state: 'loading' | 'empty' | 'error' | 'success';
  text?: string;
  image?: boolean;
}

export const enum State {
  LOADING = 'loading',
  EMPTY = 'empty',
  ERROR = 'error',
  SUCCESS = 'success',
}

const config = {
  loading: {
    icon: '\ue6cd;',
  },
  empty: {
    icon: '\ue760',
  },
  error: {
    icon: '\ue755',
  },
  success: {
    icon: '\ue62e',
  },
};

const StateIndicator = ({ className, state, text, image = false }: IProps) => {
  const renderState = () => {
    switch (state) {
      case 'loading':
        return <Spin />;
      case 'error':
        return (
          <div className={styles.errorBox}>
            {image && <div className={classnames(className, styles[state])} />}
            <div className={styles.errorText}>{text}</div>
          </div>
        );
      case 'success':
        return (
          <div className={styles.successBox}>
            {image && <div className={classnames(className, styles[state])} />}
            <div className={styles.successText}>{text}</div>
          </div>
        );
      default:
        return <div className={classnames(className, styles[state])} />;
    }
  };
  return <div className={classnames(className, styles.box)}>{renderState()}</div>;
};

export default memo<IProps>(StateIndicator);
