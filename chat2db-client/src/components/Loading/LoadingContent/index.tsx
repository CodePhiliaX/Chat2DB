import React, { memo, PropsWithChildren, Fragment } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import StateIndicator from '@/components/StateIndicator';

interface IProps<T> {
  className?: string;
  data?: T | null | undefined | true;
  empty?: React.ReactNode;
  handleEmpty?: boolean;
  isLoading?: boolean;
}

export default function LoadingContent<T>(props: PropsWithChildren<IProps<T>>) {
  const { children, className, data = true, handleEmpty = false, empty, isLoading } = props;
  const isEmpty = !isLoading && handleEmpty && !(data as any).length;

  const renderContent = () => {
    if (isLoading || !data) {
      return <StateIndicator state="loading" />;
    }

    if (isEmpty) {
      return empty || <StateIndicator state="empty" />;
    }

    return children;
  };

  return (
    <div className={classnames(styles.box, className)}>{
      renderContent()
    }</div>
  );
}
