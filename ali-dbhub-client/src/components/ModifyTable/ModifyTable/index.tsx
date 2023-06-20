import React, { memo, useState, createContext } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import IndexList from '@/components/ModifyTable/IndexList';
import ColumnList from '@/components/ModifyTable/ColumnList';
import BaseInfo from '@/components/ModifyTable/BaseInfo';
import { IEditTableConsole } from '@/types'

interface ITabItem {
  title: string;
  key: string;
  component: any; // TODO: 组件的Ts是什么
}

interface IProps {
  data: IEditTableConsole;
}

export const Context = createContext<IEditTableConsole | null>(null)

export default memo<IProps>(function ModifyTablePage(props) {
  const { data } = props;

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

  const [currentTab, setCurrentTab] = useState<ITabItem>(tabList[1]);

  function changeTab(item: ITabItem) {
    setCurrentTab(item)
  }

  function renderTabList() {
    return <div className={styles.tabList}>
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
  }

  return <div className={styles.page}>
    {renderTabList()}
    <Context.Provider value={data} >
      <div className={styles.main}>
        {
          tabList.map(t => {
            return <div key={t.key} className={classnames(styles.tab, { [styles.hidden]: currentTab.key !== t.key })}>
              {t.component}
            </div>
          })
        }
      </div>
    </Context.Provider>
  </div >
})