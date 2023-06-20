import React, { Children, useEffect, useState, PropsWithChildren } from 'react';
import styles from './index.less';
import { history } from 'umi';
import Iconfont from '@/components/Iconfont';
import classnames from 'classnames';
import { Button, Menu } from 'antd';
import Setting from '@/components/Setting';
import BrandLogo from '@/components/BrandLogo';
import i18n from '@/i18n';

interface IProps { }

interface INavItem {
  title: string;
  icon: string;
  path: string;
  openBrowser?: boolean;
}

const LNKConfig: INavItem[] = [
  {
    title: i18n('home.nav.database'),
    icon: '\ue609',
    path: '/'
  },
  {
    title: i18n('home.nav.myHistory'),
    icon: '\ue80a',
    path: '/sql-history'
  },
  // {
  //   title: 'chatRobot',
  //   icon: "\ue70e",
  //   path: '/chat'
  // },
  // {
  //   title: '后台管理',
  //   icon: "\ue66d",
  //   path: '/manage'
  // },
  {
    title: i18n('home.nav.github'),
    icon: '\ue885',
    path: 'https://github.com/alibaba/Chat2DB',
    openBrowser: true
  }
];

export default function BaseLayout({ children }: PropsWithChildren<IProps>) {
  const [activeNav, setActiveNav] = useState<INavItem>(LNKConfig[0]);

  useEffect(() => {
    // TODO:需要优化
    LNKConfig.map(item => {
      if (window.location.hash.indexOf(item.path) === 1) {
        setActiveNav(item);
      }
    })
    history.listen((location) => {
      if (location.pathname.indexOf('/database') === 0) {
        setActiveNav(LNKConfig[0])
        return
      }
      LNKConfig.map(item => {
        if (item.path === location.pathname) {
          setActiveNav(item)
        }
      })
    })
  }, []);

  function switchingNav(item: INavItem) {
    if (item.openBrowser) {
      window.open(item.path);
    } else {
      history.push(item.path);
      setActiveNav(item);
    }
  }

  function jumpHome() {
    history.push('/');
  }

  function stopxxx() {

  }

  return (
    <div className={styles.page}>
      <div className={styles.layoutLeft}>
        <div className={styles.dargBox}></div>
        <BrandLogo onClick={jumpHome} className={styles.brandLogo} />
        <ul className={styles.navList}>
          {LNKConfig.map((item) => {
            return (
              <li
                key={item.path}
                className={classnames({
                  [styles.activeNav]: item.path == activeNav.path,
                })}
                onClick={switchingNav.bind(null, item)}
              >
                <Iconfont className={styles.icon} code={item.icon} />
                <div>{item.title}</div>
              </li>
            );
          })}
        </ul>
        <div className={styles.footer}>
          <Setting className={styles.setIcon}></Setting>
        </div>
      </div>
      <div className={styles.layoutRight}>
        <div className={styles.main}>{children}</div>
      </div>
    </div>
  );
}
