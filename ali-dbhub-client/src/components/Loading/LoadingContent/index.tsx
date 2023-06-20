import React, { memo, PropsWithChildren } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import StateIndicator from '@/components/StateIndicator';

interface IProps<T> {
  className?: string;
  data: T | null | undefined;
  empty?: React.ReactChild;
  handleEmpty?: boolean;
}

export default function LoadingContent<T>(props: PropsWithChildren<IProps<T>>) {
  const { children, className, data, handleEmpty = false, empty } = props;
  const isLoading = !data;
  const isEmpty = !isLoading && handleEmpty && !(data as any).length;

  const renderContent = () => {
    if (isLoading) {
      return <StateIndicator state="loading" />;
    }

    if (isEmpty) {
      return empty || <StateIndicator state="empty" />;
    }

    return children;
  };

  return (
    <div className={classnames(className, styles.box)}>{renderContent()}</div>
  );
}
