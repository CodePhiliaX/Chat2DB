import React, { memo, useState, PropsWithChildren } from 'react';
import BrandLogo from '@/components/BrandLogo';
import Iconfont from '@/components/Iconfont';
import AppHeader from '@/components/AppHeader';
import Setting from '@/components/Setting';
import i18n from '@/i18n';
import classnames from 'classnames';
import { history, useLocation } from 'umi';
import styles from './index.less';
interface IProps {

}
interface INavItem {
  title: string;
  icon: string;
  path: string;
  openBrowser?: boolean;
}

const navConfig: INavItem[] = [
  {
    title: i18n('home.nav.database'),
    icon: '\uec57',
    path: '/connection'
  },
  {
    title: i18n('home.nav.myHistory'),
    icon: '\ue610',
    path: '/sql-history'
  },
  {
    title: i18n('home.nav.github'),
    icon: '\ue885',
    path: 'https://github.com/alibaba/Chat2DB',
    openBrowser: true
  },
  {
    title: '后台管理',
    icon: "\ue66d",
    path: '/manage',
  },
];

export default function HomeLayout({ children }: PropsWithChildren<IProps>) {
  const location = useLocation();
  const defaultNav = location.pathname == '/' ? navConfig[0].path : location.pathname
  const [currentNav, setCurrentNav] = useState<string>(defaultNav);

  function switchingNav(item: INavItem) {
    //TODO:
    if (item.openBrowser) {
      window.open(item.path)
    } else {
      history.push(item.path);
      setCurrentNav(item.path);
    }
  }

  return (
    <div className={styles.page}>
      <div className={styles.layoutLeft}>
        {/* <BrandLogo size={70} className={styles.brandLogo} /> */}
        <ul className={styles.navList}>
          {navConfig.map((item) => {
            return (
              <li
                key={item.path}
                className={classnames({
                  [styles.currentNav]: item.path == currentNav,
                })}
                onClick={switchingNav.bind(null, item)}
              >
                <Iconfont className={styles.icon} code={item.icon} />
                <div>{item.title}</div>
              </li>
            );
          })}
        </ul>
      </div>
      <div className={styles.layoutRight}>
        <AppHeader>
          <Setting className={styles.setting}></Setting>
        </AppHeader>
        {children}
      </div>
    </div>
  );
}
