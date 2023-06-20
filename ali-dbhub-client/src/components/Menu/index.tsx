import React, { PureComponent } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import Iconfont from '@/components/Iconfont';

export interface IMenu<T> {
  title: string;
  key: string;
  icon?: string;
  type?: T;
}
interface IProps<T> {
  className?: string;
  data?: IMenu<T>[];
  children?: React.ReactNode
}

interface IPropsMenuItem extends React.DetailedHTMLProps<React.HTMLAttributes<HTMLLIElement>, HTMLLIElement> {
  className?: string;
  children: React.ReactNode;
}

export function MenuItem({ children, ...rest }: IPropsMenuItem) {
  return <li className={styles.menuItem} {...rest}>
    {children}
  </li>
}

export default function Mune<T>({ className, data, children }: IProps<T>) {
  return <div className={classnames(className, styles.box)}>
    <ul className={styles.menuList}>
      {
        children
        ||
        data?.map(item => {
          return <li className={styles.menuItem} key={item.key}>
            {item.icon && <Iconfont code={item.icon} />}
            {item.title}
          </li>
        })
      }
    </ul>
  </div>
}
