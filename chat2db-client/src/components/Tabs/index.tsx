import React, { memo, useEffect, useState } from 'react';
import classnames from 'classnames';
import Iconfont from '@/components/Iconfont';
import lodash from 'lodash';
import styles from './index.less';

export interface IOption {
  label: string | React.ReactNode;
  value: number | string;
}

export interface IOnchangeProps {
  type: 'add' | 'delete' | 'switch';
  data?: IOption;
}

interface IProps {
  className?: string;
  tabs: IOption[] | undefined;
  activeTab?: number | string;
  onChange: (key: IOption['value']) => void;
  onEdit?: (action: 'add' | 'remove', key?: IOption['value']) => void;
  hideAdd?: boolean;
  type?: 'line'
}

export default memo<IProps>(function Tab(props) {
  const { className, tabs, onChange, onEdit, activeTab, hideAdd, type } = props;
  const [internalTabs, setInternalTabs] = useState<IOption[]>(lodash.cloneDeep(tabs || []));
  const [internalActiveTab, setInternalActiveTab] = useState<number | string | undefined>(internalTabs[0]?.value);

  useEffect(() => {
    setInternalActiveTab(activeTab);
  }, [activeTab])

  useEffect(() => {
    setInternalTabs(lodash.cloneDeep(tabs || []));
  }, [tabs])

  function deleteTab(data: IOption) {
    const newTabs = internalTabs?.filter(t => t.value !== data.value);
    setInternalTabs(newTabs);
    onEdit?.('remove', data.value)
  }

  function changeTab(data: IOption) {
    setInternalActiveTab(data.value);
    onChange(data.value);
  }

  function handelAdd() {
    onEdit?.('add')
  }

  return <div className={classnames(styles.tab, className)}>
    {
      !!internalTabs?.length &&
      <div className={styles.tabList}>
        {
          internalTabs.map((t, index) => {
            return <div
              key={t.value}
              className={
                classnames(
                  { [styles.tabItem]: type !== 'line' },
                  { [styles.tabItemLine]: type === 'line' },
                  { [styles.activeTabLine]: t.value === internalActiveTab && type === 'line' },
                  { [styles.activeTab]: t.value === internalActiveTab && type !== 'line' },
                )
              }

            >
              <div className={styles.text} key={t.value} onClick={changeTab.bind(null, t)}>
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
    {
      !hideAdd && <div className={styles.rightBox}>
        <div className={styles.addIcon} onClick={handelAdd}>
          <Iconfont code='&#xe631;'></Iconfont>
        </div>
      </div>
    }

  </div >
})
