import React, { memo, ReactNode, useState } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { Tabs as AntdTabs } from 'antd';

export interface ITab {
  label: ReactNode;
  key: string;
}

interface IProps {
  className?: string;
  tabs: ITab[];
  currentTab?: string;
  onChange: (key: string, index: number) => void;
  extra?: React.ReactNode
}

export default memo(function Tabs({ className, tabs, currentTab, onChange, extra }: IProps) {
  function myChange(key: string) {
    const index = tabs.findIndex(t => {
      return t.key === key
    })
    onChange(key, index)
  }

  return <div className={classnames(className, styles.box)}>
    <AntdTabs
      defaultActiveKey={currentTab}
      onChange={myChange}
      items={tabs}
    />
    <div className={styles.extra}>
      {extra}
    </div>
  </div>
})
