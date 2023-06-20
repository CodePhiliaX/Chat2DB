import classnames from 'classnames';
import React, { HtmlHTMLAttributes, memo, PropsWithChildren } from 'react';

import { safeAccess } from '@/utils';
import styles from './index.less';

export interface IHeaderProps<T> {
  className?: string;
}

export interface IRowProps<T> extends React.DetailedHTMLProps<React.HtmlHTMLAttributes<HTMLDivElement>, HTMLDivElement> {
  className?: string;
  data: T;
  index: number;
}

export interface IColumn<T> {
  dataKey?: keyof T;
  name?: React.ReactNode;
  renderHeader?: () => React.ReactNode;
  renderCell?: (data: T, i: number) => React.ReactNode;
  baseWidth?: number;
  maxWidth?: number;
  flex?: number;
}

export function createTableRows<T = {}>(columns: IColumn<T>[]) {

  function renderCell(c: IColumn<T>, content: React.ReactNode, key: number | string) {
    return <div key={key} className={styles.cell} style={{
      width: c.baseWidth,
      minWidth: c.baseWidth,
      flexBasis: c.baseWidth,
      flexGrow: c.flex,
      maxWidth: c.maxWidth,
    }}>{content}</div>
  }

  const Header = memo<IHeaderProps<T>>(function HeaderColumns({ className }) {
    return <div className={classnames(styles.box, className, styles.header)}>
      {columns.map((t, i) => renderCell(t, t.renderHeader ? t.renderHeader() : t.name, i))}
    </div>
  });

  const Row = memo<IRowProps<T>>(function RowColumns({ className, data, index, ...res }) {
    return <div {...res} className={classnames(styles.box, className)}>
      {columns.map((t, i) => renderCell(t, t.renderCell ? t.renderCell(data, index) : (t.dataKey ?? (data[t.dataKey as any] ?? safeAccess(data, t.dataKey as any))), i))}
    </div>
  });

  return { Header, Row };
}
