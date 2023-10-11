import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { connect } from 'umi';
import { Dropdown } from 'antd';
import classnames from 'classnames';

import Iconfont from '@/components/Iconfont';
import BrandLogo from '@/components/BrandLogo';

import { findObjListValue } from '@/utils';
import { getUser, userLogout } from '@/service/user';
import { INavItem } from '@/typings/main';
import { ILoginUser } from '@/typings/user';
import i18n from '@/i18n';

// ----- model -----
import { IMainPageType } from '@/models/mainPage';
import { IWorkspaceModelType } from '@/models/workspace';
import { IConnectionModelType } from '@/models/connection';

// ----- block -----
import Workspace from './workspace';
import Dashboard from './dashboard';
import Connection from './connection';
import Team from './team';
import Setting from '@/blocks/Setting';

import styles from './index.less';

const navConfig: INavItem[] = [
  {
    key: 'workspace',
    icon: '\ue616',
    iconFontSize: 16,
    isLoad: false,
    component: <Workspace />,
  },
  {
    key: 'dashboard',
    icon: '\ue629',
    iconFontSize: 24,
    isLoad: false,
    component: <Dashboard />,
  },
  {
    key: 'connections',
    icon: '\ue622',
    iconFontSize: 20,
    isLoad: false,
    component: <Connection />,
  },
  {
    key: 'github',
    icon: '\ue885',
    iconFontSize: 26,
    isLoad: false,
    openBrowser: 'https://github.com/chat2db/Chat2DB/',
  },
];

const initPageIndex = navConfig.findIndex((t) => `${t.key}` === localStorage.getItem('curPage'));
const activeIndex = initPageIndex > -1 ? initPageIndex : 2;
navConfig[activeIndex].isLoad = true;

interface IProps {
  mainModel: IMainPageType['state'];
  workspaceModel: IWorkspaceModelType['state'];
  connectionModel: IConnectionModelType['state'];
  dispatch: any;
}

function MainPage(props: IProps) {
  const navigate = useNavigate();
  const { mainModel, dispatch } = props;
  const { curPage } = mainModel;
  const [activeNav, setActiveNav] = useState<INavItem>(navConfig[activeIndex]);
  const [userInfo, setUserInfo] = useState<ILoginUser>();

  useEffect(() => {
    getUser().then((res) => {
      if (res) {
        setUserInfo(res);
        const hasTeamIcon = navConfig.find((i) => i.key === 'team');
        if (res.admin && !hasTeamIcon) {
          navConfig.splice(3, 0, {
            key: 'team',
            icon: '\ue64b',
            iconFontSize: 24,
            isLoad: false,
            component: <Team />,
          });
          if (localStorage.getItem('curPage') === 'team') {
            setActiveNav(navConfig[3]);
          }
        }
        if (!res.admin && hasTeamIcon) {
          navConfig.splice(3, 1);
          if (localStorage.getItem('curPage') === 'team') {
            setActiveNav(navConfig[2]);
          }
        }
      }
    });
  }, []);

  useEffect(() => {
    dispatch({
      type: 'connection/fetchConnectionList',
    });
    dispatch({
      type: 'connection/fetchConnectionEnvList',
    });
  }, []);

  useEffect(() => {
    // activeNav 发生变化，同步到全局状态管理
    activeNav.isLoad = true;
    dispatch({
      type: 'mainPage/updateCurPage',
      payload: activeNav.key,
    });
    // activeNav 发生变化 如果没有选中连接并且不在connections 那么需要跳转到 连接页面
    // if (!curConnection?.id && activeNav.key !== 'connections') {
    //   setActiveNav(navConfig[2]);
    // }
    // activeNav 变化 同步地址栏变化
    // change url，but no page refresh
    // window.history.pushState({}, "", `/#/${activeNav.key}`);
  }, [activeNav]);

  useEffect(() => {
    // 全局状态curPage发生变化，activeNav 需要同步变化
    if (curPage && curPage !== activeNav.key) {
      const newActiveNav = navConfig[findObjListValue(navConfig, 'key', curPage)];
      setActiveNav(newActiveNav);
    }
    localStorage.setItem('curPage', curPage);
  }, [curPage]);

  function switchingNav(item: INavItem) {
    if (item.openBrowser) {
      window.open(item.openBrowser, '_blank');
    } else {
      setActiveNav(item);
    }
  }

  const handleLogout = () => {
    userLogout().then((res) => {
      setUserInfo(undefined);
      navigate('/login');
    });
  };

  const renderUser = () => {
    return (
      <Dropdown
        menu={{
          items: [
            {
              key: '1',
              label: <div onClick={handleLogout}>{i18n('login.text.logout')}</div>,
            },
          ],
        }}
        placement="bottomRight"
        arrow={{ pointAtCenter: true }}
      >
        <Iconfont code="&#xe64c;" className={styles.questionIcon} />
      </Dropdown>
    );
  };

  return (
    <div className={styles.page}>
      <div className={styles.layoutLeft}>
        <BrandLogo size={40} onClick={() => {}} className={styles.brandLogo} />
        <ul className={styles.navList}>
          {navConfig.map((item) => {
            return (
              <li
                key={item.key}
                className={classnames({
                  [styles.activeNav]: item.key == activeNav.key,
                })}
                onClick={() => switchingNav(item)}
              >
                <Iconfont style={{ fontSize: `${item.iconFontSize}px` }} className={styles.icon} code={item.icon} />
              </li>
            );
          })}
        </ul>
        <div className={styles.footer}>
          {/* <Iconfont
            code="&#xe67c;"
            className={styles.questionIcon}
            onClick={() => {
              window.open('https://github.com/chat2db/chat2db/wiki');
            }}
          /> */}
          {userInfo ? renderUser() : null}
          <Setting className={styles.setIcon} />
        </div>
      </div>
      <div className={styles.layoutRight}>
        {navConfig.map((item) => {
          return (
            <div key={item.key} className={styles.componentBox} hidden={activeNav.key !== item.key}>
              {item.isLoad ? item.component : null}
            </div>
          );
        })}
      </div>
    </div>
  );
}

export default connect(
  ({
    mainPage,
    workspace,
    connection,
  }: {
    mainPage: IMainPageType;
    workspace: IWorkspaceModelType;
    connection: IConnectionModelType;
  }) => ({
    mainModel: mainPage,
    workspaceModel: workspace,
    connectionModel: connection,
  }),
)(MainPage);
