import React, { memo, useState, useContext } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import Iconfont from '../Iconfont';
import type { MenuProps } from 'antd';
import { Menu } from 'antd';
import { IDatabase, ITreeNode } from '@/types'
import { databaseType, DatabaseTypeCode } from '@/utils/constants';
import { DatabaseContext } from '@/context/database'

interface IProps {
  className?: string;
  getAddTreeNode: (data: ITreeNode) => void;
}

type MenuItem = {
  label: React.ReactNode,
  key: React.Key | null,
  icon?: React.ReactNode,
  children?: MenuItem[],
}

function getItem(
  label: React.ReactNode,
  key: React.Key | null,
  icon?: React.ReactNode,
  children?: MenuItem[],
): MenuItem {
  return {
    label,
    key,
    icon,
    children,
  } as MenuItem;
}

const newDataSourceChildren = Object.keys(databaseType).map(t => {
  const source: IDatabase = databaseType[t];
  return getItem(source.name, source.code, <Iconfont className={styles.databaseTypeIcon} code={source.icon} />)
})

type IGlobalAddMenuItem = {

} & MenuItem


const globalAddMenuList: IGlobalAddMenuItem[] = [
  // {
  //   label: '新建控制台',
  //   key: 'newConsole',
  //   icon: <Iconfont code='&#xe619;' />
  // },
  {
    label: '新建数据源',
    key: 'newDataSource',
    icon: <Iconfont code='&#xe631;' />,
    children: newDataSourceChildren
  },
]

const items: MenuItem[] = newDataSourceChildren

export default memo<IProps>(function GlobalAddMenu(props) {
  const { className, getAddTreeNode } = props;
  const { model, setEditDataSourceData } = useContext(DatabaseContext);

  const onClickMenuNode: MenuProps['onClick'] = (e) => {
    setEditDataSourceData({
      dataType: e.keyPath[0] as DatabaseTypeCode,
    })
  };

  function submitCallback(data: ITreeNode) {
    getAddTreeNode(data);
  }

  return <div className={classnames(styles.box, className)}>
    <Menu onClick={onClickMenuNode} mode="vertical" items={items as any} />
    {/* {((!!dataSourceType && isModalVisible) || editDataSourceData) &&
      <CreateConnection
        submitCallback={submitCallback}
        dataSourceType={editDataSourceData.dataType || dataSourceType}
        dataSourceData={editDataSourceData}
        onCancel={onCancel}
      />
    } */}
  </div>
})
