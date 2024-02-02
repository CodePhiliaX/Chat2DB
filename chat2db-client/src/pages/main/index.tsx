import React, { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Dropdown, Flex, Tooltip } from 'antd';
import classnames from 'classnames';

import Iconfont from '@/components/Iconfont';

import i18n from '@/i18n';
import { userLogout } from '@/service/user';
import { INavItem } from '@/typings/main';
import { IRole } from '@/typings/user';

// ----- store -----
import { getConnectionList, getConnectionEnvList } from '@/store/connection';
import { useUserStore, setCurUser } from '@/store/user';
import { useGlobalStore } from '@/store/global';

// ----- component -----
import CustomLayout from '@/components/CustomLayout';

// ----- block -----
import Workspace from './workspace';
import Dashboard from './dashboard';
import Connection from './connection';
import Team from './team';
import Setting from '@/blocks/Setting';

import { useUpdateEffect } from '@/hooks';
import { useStyle } from './style';
import { IconButton, Logo } from '@chat2db/ui';
import { isMac } from '@/utils/env';
import { Database, Github, Image, User2, WorkflowIcon, Settings } from 'lucide-react';

const initNavConfig: INavItem[] = [
  {
    key: 'workspace',
    icon: WorkflowIcon,
    iconFontSize: 16,
    isLoad: false,
    component: <Workspace />,
    name: i18n('workspace.title'),
  },
  {
    key: 'dashboard',
    icon: Image,
    iconFontSize: 24,
    isLoad: false,
    component: <Dashboard />,
    name: i18n('dashboard.title'),
  },
  {
    key: 'connections',
    icon: Database,
    iconFontSize: 20,
    isLoad: false,
    component: <Connection />,
    name: i18n('connection.title'),
  },
  {
    key: 'github',
    icon: Github,
    iconFontSize: 26,
    isLoad: false,
    openBrowser: 'https://github.com/chat2db/Chat2DB/',
    name: 'Github',
  },
];

function MainPage() {
  const navigate = useNavigate();
  const { styles, cx } = useStyle({ isMac });
  const { userInfo } = useUserStore((state) => {
    return {
      userInfo: state.curUser,
    };
  });
  const { mainPageActiveTab, setMainPageActiveTab, setAppTitleBarRightComponent } = useGlobalStore((s) => {
    return {
      mainPageActiveTab: s.mainPageActiveTab,
      setMainPageActiveTab: s.setMainPageActiveTab,
      setAppTitleBarRightComponent: s.setAppTitleBarRightComponent,
    };
  });
  const [navConfig, setNavConfig] = useState<INavItem[]>(initNavConfig);
  const [activeNavKey, setActiveNavKey] = useState<string>(
    __ENV__ === 'desktop' ? mainPageActiveTab : window.location.pathname.split('/')[1] || mainPageActiveTab,
  );

  // 当页面在workspace时，显示自定义布局
  useEffect(() => {
    if (mainPageActiveTab === 'workspace') {
      setAppTitleBarRightComponent(<CustomLayout />);
    } else {
      setAppTitleBarRightComponent(false);
    }
    return () => {
      setAppTitleBarRightComponent(false);
    };
  }, [mainPageActiveTab]);

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
    <div className={styles.container}>
      <div className={styles.leftContainer}>
        <div className={styles.navContainer}>
          <Logo size={36} className={styles.logo} />
          {navConfig.map((item, index) => (
            <IconButton
              isActive={index === 0}
              key={item.key}
              size="large"
              title={item.name}
              icon={item.icon}
              tooltipPlacement="right"
              onClick={() => switchingNav(item.key)}
            />
          ))}
        </div>

        <div className={styles.settingContainer}>
          {userInfo?.roleCode !== IRole.DESKTOP && <IconButton title="个人中心" icon={User2} />}
          <IconButton icon={Settings} onClick={() => switchingNav('setting')} />
        </div>
      </div>
      <div className={styles.rightContianer}>
        {navConfig.map((item) => (
          <div key={item.key} className={styles.componentBox} hidden={activeNavKey !== item.key}>
            {item.isLoad ? item.component : null}
          </div>
        ))}
      </div>
    </div>
  );
}

export default MainPage;
