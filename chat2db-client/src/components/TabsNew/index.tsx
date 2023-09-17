import React, { memo, useEffect, useState, Fragment } from 'react';
import classnames from 'classnames';
import Iconfont from '@/components/Iconfont';
import styles from './index.less';

export interface ITabItem {
  prefixIcon?: string;
  label: React.ReactNode;
  key: number | string;
  children?: React.ReactNode;
}

export interface IOnchangeProps {
  type: 'add' | 'delete' | 'switch';
  data?: ITabItem;
}

interface IProps {
  className?: string;
  items: ITabItem[] | undefined;
  activeKey?: number | string;
  onChange?: (key: string | number | undefined) => void;
  onEdit?: (action: 'add' | 'remove', data?: ITabItem) => void;
  hideAdd?: boolean;
  type?: 'line';
  editableName?: boolean;
  editableNameOnBlur?: (option: ITabItem) => void;
}

export default memo<IProps>(function Tabs(props) {
  const { className, items, onChange, onEdit, activeKey, hideAdd, type, editableName, editableNameOnBlur } = props;
  const [internalTabs, setInternalTabs] = useState<ITabItem[]>([]);
  const [internalActiveTab, setInternalActiveTab] = useState<number | string | undefined>();
  const [editingTab, setEditingTab] = useState<ITabItem['key'] | undefined>();

  useEffect(() => {
    if (activeKey !== null && activeKey !== undefined) {
      setInternalActiveTab(activeKey);
    }
  }, [activeKey])

  useEffect(() => {
    setInternalTabs(items || []);
    if (items?.length && internalActiveTab === undefined) {
      setInternalActiveTab(items[0].key);
    }
  }, [items])

  useEffect(() => {
    onChange?.(internalActiveTab);
  }, [internalActiveTab])

  function deleteTab(data: ITabItem) {
    const newInternalTabs = internalTabs?.filter(t => t.key !== data.key);
    let activeKey = internalActiveTab;
    // 删掉的是当前激活的tab，那么就切换到前一个,如果前一个没有就切换到后一个
    if (data.key === internalActiveTab) {
      const index = internalTabs.findIndex(t => t.key === data.key);
      if (index === 0) {
        activeKey = internalTabs[1]?.key
      } else {
        activeKey = internalTabs[index - 1]?.key
      }
    }
    changeTab(activeKey);
    setInternalTabs(newInternalTabs);
    onEdit?.('remove', data)
  }

  function changeTab(key: string | number | undefined) {
    setInternalActiveTab(key);
  }

  function handelAdd() {
    onEdit?.('add')
  }

  function onDoubleClick(t: ITabItem) {
    if (editableName) {
      setEditingTab(t.key)
    }
  }

  function renderTabItem(t: ITabItem, index: number) {
    function inputOnChange(value: string) {
      internalTabs[index].label = value
      setInternalTabs([...internalTabs])
    }

    function onBlur() {
      editableNameOnBlur?.(t);
      setEditingTab(undefined);
    }

    return <div
      onDoubleClick={() => { onDoubleClick(t) }}
      key={t.key}
      className={
        classnames(
          { [styles.tabItem]: type !== 'line' },
          { [styles.tabItemLine]: type === 'line' },
          { [styles.activeTabLine]: t.key === internalActiveTab && type === 'line' },
          { [styles.activeTab]: t.key === internalActiveTab && type !== 'line' },
        )
      }
    >
      {
        t.key === editingTab ?
          <input value={t.label as string} onChange={(e) => { inputOnChange(e.target.value) }} className={styles.input} autoFocus onBlur={onBlur} type="text" />
          :
          <div className={styles.textBox} key={t.key} onClick={changeTab.bind(null, t.key)}>
            {t.prefixIcon && <Iconfont className={styles.prefixIcon} code={t.prefixIcon} />}
            <div className={styles.text}>
              {t.label}
            </div>
          </div>
      }
      <div className={styles.icon} onClick={deleteTab.bind(null, t)}>
        <Iconfont code='&#xe634;' />
      </div>
    </div>
  }

  return <div className={classnames(styles.tabBox, className)}>
    <div className={styles.tabsNav}>
      {
        !!internalTabs?.length &&
        <div className={styles.tabList}>
          {
            internalTabs.map((t, index) => {
              return renderTabItem(t, index)
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
    </div>
    <div className={styles.tabsContent}>
      {
        internalTabs?.map(t => {
          return <div key={t.key} className={classnames(styles.tabsContentItem, { [styles.tabsContentItemActive]: t.key === internalActiveTab })}>
            {t.children}
          </div>
        })
      }
    </div>
  </div >
})
