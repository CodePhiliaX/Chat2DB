import React, { memo, useEffect, useState } from 'react';
import classnames from 'classnames';
import Iconfont from '@/components/Iconfont';
import lodash from 'lodash';
import styles from './index.less';

export interface IOption {
  label: string;
  value: number;
}

export interface IOnchangeProps {
  type: 'add' | 'delete' | 'switch';
  data?: IOption;
}

interface IProps {
  className?: string;
  tabs: IOption[];
  activeTab?: number;
  onChange: (key: IOption['value']) => void;
  onEdit: (action: 'add' | 'remove', key?: IOption['value']) => void;
}

export default memo<IProps>(function Tab(props) {
  const { className, tabs, onChange, onEdit, activeTab } = props;
  const [internalTabs, setInternalTabs] = useState<IOption[]>(lodash.cloneDeep(tabs));
  const [internalActiveTab, setInternalActiveTab] = useState<number | undefined>(internalTabs[0]?.value);

  useEffect(() => {
    setInternalActiveTab(activeTab)
  }, [activeTab])

  useEffect(() => {
    setInternalTabs(lodash.cloneDeep(tabs));
  }, [tabs])

  function deleteTab(data: IOption) {
    const newTabs = internalTabs?.filter(t => t.value !== data.value);
    setInternalTabs(newTabs);
    onEdit('remove', data.value)
  }

  function changeTab(data: IOption) {
    setInternalActiveTab(data.value);
    onChange(data.value)

  }

  function handelAdd() {
    onEdit('add')
  }

  return <div className={classnames(styles.tab, className)}>
    {
      !!internalTabs?.length &&
      <div className={styles.tabList}>
        {
          internalTabs.map(t => {
            return <div
              className={classnames(styles.tabItem, { [styles.activeTab]: t.value === internalActiveTab })}
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
