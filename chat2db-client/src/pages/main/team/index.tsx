import React, { useMemo, useState } from 'react';
import { ApiOutlined, UserOutlined, TeamOutlined } from '@ant-design/icons';
import { Tabs } from 'antd';
import styles from './index.less';
import DataSourceManagement from './datasource-management';
import UserManagement from './user-management';
import TeamManagement from './team-management';

const Team = () => {
  const [activeKey, setActiveKey] = useState<string>('2');
  const tabList = useMemo(
    () => [
      {
        label: '共享链接管理',
        icon: <ApiOutlined />,
        children: <DataSourceManagement />,
      },
      {
        label: '用户管理',
        icon: <UserOutlined />,
        children: <UserManagement />,
      },
      {
        label: '团队管理',
        icon: <TeamOutlined />,
        children: <TeamManagement />,
      },
    ],
    [],
  );

  return (
    <div className={styles.teamWrapper}>
      <Tabs
        activeKey={activeKey}
        onChange={(activeKey) => setActiveKey(activeKey)}
        items={tabList.map((tab, index) => {
          return {
            key: String(index),
            label: (
              <span>
                {tab.icon}
                {tab.label}
              </span>
            ),
            children: tab.children,
          };
        })}
      />
    </div>
  );
};

export default Team;
