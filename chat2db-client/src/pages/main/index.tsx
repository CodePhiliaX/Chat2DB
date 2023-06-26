import React, { useEffect, useState, PropsWithChildren } from 'react';
import i18n from '@/i18n';
import { Button } from 'antd';
import { history, connect } from 'umi';
import classnames from 'classnames';
import Setting from '@/blocks/Setting';
import Iconfont from '@/components/Iconfont';
import BrandLogo from '@/components/BrandLogo';
import { MainState } from '@/models/main';
import { findObjListValue } from '@/utils'

import DataSource from './connection';
import Workspace from './workspace';
import Dashboard from './dashboard';
import Chat from './chat';

import styles from './index.less';
import { INavItem } from '@/typings/main';
const navConfig: INavItem[] = [
  {
    key: 'workspace',
    icon: '\ue616',
    iconFontSize: 16,
    component: <Workspace />,
  },
  {
    key: 'dashboard',
    icon: '\ue629',
    iconFontSize: 24,
    component: <Dashboard />,
  },
  {
    key: 'connections',
    icon: '\ue622',
    iconFontSize: 20,
    component: <DataSource />,
  },
  // {
  //   key: 'github',
  //   icon: '\ue885',
  //   openBrowser: 'https://github.com/alibaba/Chat2DB',
  // },
];

const initPageIndex = navConfig.findIndex(t => `/${t.key}` === window.location.pathname);

interface IProps {
  mainModel: MainState;
  dispatch: any;
}

function MainPage(props: IProps) {
  const { mainModel, dispatch } = props;
  const { curPage } = mainModel;
  const [activeNav, setActiveNav] = useState<INavItem>(navConfig[initPageIndex > 0 ? initPageIndex : 0]);

  useEffect(() => {
    dispatch({
      type: 'mainPage/updateCurPage',
      payload: activeNav.key
    })
  }, [activeNav])

  useEffect(() => {
    if (curPage !== activeNav.key) {
      const activeNav = navConfig[findObjListValue(navConfig, 'key', curPage)]
      setActiveNav(activeNav)
    }
  }, [curPage])

  function switchingNav(item: INavItem) {
    // change url，but no page refresh
    window.history.pushState({}, "", item.key);
    if (item.openBrowser) {
      window.open(item.openBrowser);
    } else {
      setActiveNav(item);
    }
  }

  useEffect(() => {

  }, [])

  return (
    <div className={styles.page}>
      <div className={styles.layoutLeft}>
        <BrandLogo size={40} onClick={() => { }} className={styles.brandLogo} />
        <ul className={styles.navList}>
          {navConfig.map((item, index) => {
            return (
              <li
                key={item.key}
                className={classnames({
                  [styles.activeNav]: item.key == activeNav.key,
                })}
                onClick={switchingNav.bind(null, item)}
              >
                <Iconfont style={{ fontSize: `${item.iconFontSize}px` }} className={styles.icon} code={item.icon} />
                {/* <div>{item.title}</div> */}
              </li>
            );
          })}
        </ul>
        <div className={styles.footer}>
          <Setting className={styles.setIcon}></Setting>
        </div>
      </div>
      <div className={styles.layoutRight}>
        {navConfig.map((item) => {
          return (
            <div
              key={item.key}
              className={styles.componentBox}
              hidden={activeNav.key !== item.key}
            >
              {item.component}
            </div>
          );
        })}
      </div>
    </div>
  );
}

export default connect(({ mainPage }: { mainPage: MainState }) => ({
  mainModel: mainPage,
}))(MainPage);
