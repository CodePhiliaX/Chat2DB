import React, { memo, useState } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import IndexList from './IndexList';
import ColumnList from './ColumnList';
import BaseInfo from './BaseInfo';

interface IProps {
  className?: string;
}

interface ITabItem {
  title: string;
  key: string;
  component: any; // TODO: 组件的Ts是什么
}

const tabList: ITabItem[] = [
  {
    title: '基本信息',
    key: 'basic',
    component: <BaseInfo></BaseInfo>
  },
  {
    title: '列信息',
    key: 'column',
    component: <ColumnList></ColumnList>
  },
  {
    title: '索引信息',
    key: 'index',
    component: <IndexList></IndexList>
  },
]

export default memo<IProps>(function DatabaseTableEditor(props) {
  const { className } = props;
  const [currentTab, setCurrentTab] = useState<ITabItem>(tabList[1]);

  function changeTab(item: ITabItem) {
    setCurrentTab(item)
  }

  return <div className={classnames(styles.box, className)}>
    <div className={styles.tabList}>
      {
        tabList.map((item, index) => {
          return <div
            key={item.key}
            onClick={changeTab.bind(null, item)}
            className={classnames(styles.tabItem, currentTab.key == item.key ? styles.currentTab : '')}
          >
            {item.title}
          </div>
        })
      }
    </div>
    <div className={styles.main}>
      {
        tabList.map(t => {
          return <div key={t.key} className={classnames(styles.tab, { [styles.hidden]: currentTab.key !== t.key })}>
            {t.component}
          </div>
        })
      }
    </div>
  </div>
})
