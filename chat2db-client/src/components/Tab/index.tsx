import React, { memo, useEffect, useState } from 'react';
import classnames from 'classnames';
import Iconfont from '@/components/Iconfont';
import lodash from 'lodash'
import styles from './index.less';

export interface IOption {
  label: string;
  value: string | number;
}

export interface IOnchangeProps {
  type: 'add' | 'delete' | 'switch';
  data?: IOption;
}

interface IProps {
  className?: string;
  tabs: IOption[];
  onchange: (props: IOnchangeProps) => {}
}

export default memo<IProps>(function Tab(props) {
  const { className, tabs, onchange } = props;
  const [internalTabs, setInternalTabs] = useState<IOption[]>(lodash.cloneDeep(tabs));
  const [activeTab, setActiveTab] = useState(internalTabs[0]?.value);

  useEffect(() => {
    setInternalTabs(lodash.cloneDeep(tabs));
  }, [tabs])

  function deleteTab(data: IOption) {
    const newTabs = internalTabs?.filter(t => t.value !== data.value);
    setInternalTabs(newTabs);
    onchange({
      type: 'delete',
      data: data
    })
  }

  function changeTab(data: IOption) {
    setActiveTab(data.value);
    onchange({
      type: 'switch',
      data: data
    })
  }

  function handelAdd() {
    onchange({
      type: 'add'
    })
  }

  return <div className={classnames(styles.tab, className)}>
    {
      !!internalTabs?.length &&
      <div className={styles.tabList}>
        {
          internalTabs.map(t => {
            return <div
              className={classnames(styles.tabItem, { [styles.activeTab]: t.value === activeTab })}
              key={t.value}
              onClick={changeTab.bind(null, t)}
            >
              <div className={styles.text}>
                {t.label}
              </div>
              <div className={styles.icon} onClick={deleteTab.bind(null, t)}>
                <Iconfont code='&#xe634;' />
              </div>
            </div>
          })
        }
      </div>
    }
    <div className={styles.rightBox}>
      <div className={styles.addIcon} onClick={handelAdd}>
        <Iconfont code='&#xe631;'></Iconfont>
      </div>
    </div>
  </div >
})
