import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Dropdown, Tooltip } from 'antd';
import classnames from 'classnames';

import Iconfont from '@/components/Iconfont';
import BrandLogo from '@/components/BrandLogo';

import i18n from '@/i18n';
import { userLogout } from '@/service/user';
import { INavItem } from '@/typings/main';
import { IRole } from '@/typings/user';

// ----- hooks -----
import getConnectionEnvList from './functions/getConnection';

// ----- store -----
import { useMainStore, setMainPageActiveTab } from '@/pages/main/store/main';
import { getConnectionList } from '@/pages/main/store/connection';
import { useUserStore, setCurUser } from '@/store/user';

// ----- block -----
import Workspace from './workspace';
import Dashboard from './dashboard';
import Connection from './connection';
import Team from './team';
import Setting from '@/blocks/Setting';

import styles from './index.less';
import { useUpdateEffect } from '@/hooks';

const initNavConfig: INavItem[] = [
  {
    key: 'workspace',
    icon: '\ue616',
    iconFontSize: 16,
    isLoad: false,
    component: <Workspace />,
    name: i18n('workspace.title'),
  },
  {
    key: 'dashboard',
    icon: '\ue629',
    iconFontSize: 24,
    isLoad: false,
    component: <Dashboard />,
    name: i18n('dashboard.title'),
  },
  {
    key: 'connections',
    icon: '\ue622',
    iconFontSize: 20,
    isLoad: false,
    component: <Connection />,
    name: i18n('connection.title'),
  },
  {
    key: 'github',
    icon: '\ue885',
    iconFontSize: 26,
    isLoad: false,
    openBrowser: 'https://github.com/chat2db/Chat2DB/',
    name: 'Github',
  },
];

function MainPage() {
  const navigate = useNavigate();
  const { userInfo } = useUserStore(state => {
    return {
      userInfo: state.curUser
    }
  });
  const [navConfig, setNavConfig] = useState<INavItem[]>(initNavConfig);
  const mainPageActiveTab = useMainStore((state) => state.mainPageActiveTab);
  const [activeNavKey, setActiveNavKey] = useState<string>(
    __ENV__ === 'desktop' ? mainPageActiveTab : window.location.pathname.split('/')[1] || mainPageActiveTab,
  );

  useEffect(() => {
    handleInitPage();
    getConnectionList();
    getConnectionEnvList();
  }, []);

  useUpdateEffect(() => {
    switchingNav(mainPageActiveTab);
  }, [mainPageActiveTab]);

  // 切换tab
  useEffect(() => {
    // 获取当前地址栏的tab
    const activeIndex = navConfig.findIndex((t) => `${t.key}` === activeNavKey);
    if (activeIndex > -1) {
      navConfig[activeIndex].isLoad = true;
      setNavConfig([...navConfig]);
      if (__ENV__ !== 'desktop') {
        const href = window.location.origin + '/' + activeNavKey;
        window.history.pushState({}, '', href);
      }
    }
  }, [activeNavKey]);

  const handleInitPage = async () => {
    const cloneNavConfig = [...navConfig];
    if (userInfo) {
      const hasTeamIcon = cloneNavConfig.find((i) => i.key === 'team');
      if (userInfo.admin && !hasTeamIcon) {
        cloneNavConfig.splice(3, 0, {
          key: 'team',
          icon: '\ue64b',
          iconFontSize: 24,
          isLoad: activeNavKey === 'team', // 如果当前是team，直接加载
          component: <Team />,
          name: i18n('team.title'),
        });
      }
      if (!userInfo.admin && hasTeamIcon) {
        cloneNavConfig.splice(3, 1);
      }
    }
    setNavConfig([...cloneNavConfig]);
  };

  const switchingNav = (key: string) => {
    if (key === 'github') {
      window.open('https://github.com/chat2db/Chat2DB/', '_blank');
    } else {
      setActiveNavKey(key);
      setMainPageActiveTab(key);
    }
  };

  const handleLogout = () => {
    userLogout().then(() => {
      setCurUser(undefined);
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
              label: (
                <div className={styles.userDropdown} onClick={handleLogout}>
                  <Iconfont code="&#xe6b2;" />
                  {i18n('login.text.logout')}
                </div>
              ),
            },
          ],
        }}
        placement="bottomRight"
        trigger={['click']}
      >
        <div className={styles.userBox}>
          <Iconfont code="&#xe64c;" className={styles.questionIcon} />
        </div>
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
              <Tooltip key={item.key} placement="right" title={item.name}>
                <li
                  className={classnames({
                    [styles.activeNav]: item.key == activeNavKey,
                  })}
                  onClick={() => switchingNav(item.key)}
                >
                  <Iconfont size={item.iconFontSize} className={styles.icon} code={item.icon} />
                </li>
              </Tooltip>
            );
          })}
        </ul>
        <div className={styles.footer}>
          <Tooltip placement="right" title="个人中心">
            {userInfo?.roleCode !== IRole.DESKTOP ? renderUser() : null}
          </Tooltip>
          <Setting className={styles.setIcon} />
        </div>
      </div>
      <div className={styles.layoutRight}>
        {navConfig.map((item) => {
          return (
            <div key={item.key} className={styles.componentBox} hidden={activeNavKey !== item.key}>
              {item.isLoad ? item.component : null}
            </div>
          );
        })}
      </div>
    </div>
  );
}

export default MainPage;
