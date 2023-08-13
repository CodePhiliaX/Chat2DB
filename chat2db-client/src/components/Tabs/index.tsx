import React, { memo, useEffect, useState } from 'react';
import classnames from 'classnames';
import Iconfont from '@/components/Iconfont';
import lodash from 'lodash';
import styles from './index.less';

export interface IOption {
  prefixIcon?: string;
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
  type?: 'line';
  editableName?: boolean;
  editableNameOnBlur?: (option: IOption) => void;
}

export default memo<IProps>(function Tab(props) {
  const { className, tabs, onChange, onEdit, activeTab, hideAdd, type, editableName, editableNameOnBlur } = props;
  const [internalTabs, setInternalTabs] = useState<IOption[]>(lodash.cloneDeep(tabs || []));
  const [internalActiveTab, setInternalActiveTab] = useState<number | string | undefined>(internalTabs[0]?.value);
  const [editingTab, setEditingTab] = useState<IOption['value'] | undefined>();

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

  function onDoubleClick(t: IOption) {
    if (editableName) {
      setEditingTab(t.value)
    }
  }


  function renderTabItem(t: IOption, index: number) {
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
      {
        t.value === editingTab ?
          <input value={t.label as string} onChange={(e) => { inputOnChange(e.target.value) }} className={styles.input} autoFocus onBlur={onBlur} type="text" />
          :
          <div className={styles.textBox} key={t.value} onClick={changeTab.bind(null, t)}>
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

  return <div className={classnames(styles.tab, className)}>
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
  </div >
})
