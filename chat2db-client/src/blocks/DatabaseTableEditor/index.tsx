import React, { memo, useRef, useState, createContext, useEffect, forwardRef, useMemo } from 'react';
import { Button, Form } from 'antd';
import styles from './index.less';
import classnames from 'classnames';
import IndexList from './IndexList';
import ColumnList from './ColumnList';
import BaseInfo, { IBaseInfoRef } from './BaseInfo';
import sqlService from '@/service/sql';
import { IDatabaseTableDetail } from '@/typings';

interface IProps {
  dataSourceId: number,
  databaseName: string,
  schemaName: string | undefined,
  tableName: string
}

interface ITabItem {
  title: string;
  key: string;
  component: any; // TODO: 组件的Ts是什么
}

interface IContext extends IProps {
  tableDetails: IDatabaseTableDetail
}

export const Context = createContext<IContext>({} as any);

export default memo<IProps>(function DatabaseTableEditor(props) {
  const { databaseName, dataSourceId, tableName, schemaName } = props;
  const [tableDetails, setTableDetails] = useState<IDatabaseTableDetail>({} as any);
  const baseInfoRef = useRef<IBaseInfoRef>(null);
  const tabList = useMemo(() => {
    return [
      {
        title: '基本信息',
        key: 'basic',
        component: <BaseInfo ref={baseInfoRef} />
      },
      {
        title: '列信息',
        key: 'column',
        component: <ColumnList />
      },
      {
        title: '索引信息',
        key: 'index',
        component: <IndexList />
      },
    ]
  }, [])
  const [currentTab, setCurrentTab] = useState<ITabItem>(tabList[0]);


  function changeTab(item: ITabItem) {
    setCurrentTab(item)
  }

  useEffect(() => {
    let params = {
      databaseName,
      dataSourceId,
      tableName,
      schemaName,
      refresh: true
    }
    sqlService.getTableDetails(params).then(res => {
      setTableDetails(res)
    })
  }, [])

  function submit() {
    console.log(baseInfoRef.current?.getBaseInfo())
  }

  return <Context.Provider value={{
    ...props,
    tableDetails
  }}>
    <div className={classnames(styles.box)}>
      <div className={styles.header}>
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
        <div className={styles.saveButton}>
          <Button type="primary" onClick={submit}>保存</Button>
        </div>
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
  </Context.Provider>

})
