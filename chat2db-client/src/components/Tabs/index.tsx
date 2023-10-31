import React, { memo, useEffect, useState, useRef } from 'react';
import classnames from 'classnames';
import Iconfont from '@/components/Iconfont';
import styles from './index.less';
import { Popover } from 'antd';

export interface ITabItem {
  prefixIcon?: string | React.ReactNode;
  label: React.ReactNode;
  key: number | string;
  popover?: string | React.ReactNode;
  children?: React.ReactNode;
  editableName?: boolean;
  canClosed?: boolean;
  styles?: React.CSSProperties;
}

export interface IOnchangeProps {
  type: 'add' | 'delete' | 'switch';
  data?: ITabItem;
}

interface IProps {
  className?: string;
  items?: ITabItem[];
  activeKey?: number | string | null;
  onChange?: (key: string | number | null) => void;
  onEdit?: (action: 'add' | 'remove', data?: ITabItem, list?: ITabItem[]) => void;
  hideAdd?: boolean;
  editableNameOnBlur?: (option: ITabItem) => void;
  concealTabHeader?: boolean;
  // 最后一个tab不能关闭
  lastTabCannotClosed?: boolean;
}

export default memo<IProps>((props) => {
  const {
    className,
    items,
    onChange,
    onEdit,
    activeKey,
    hideAdd,
    lastTabCannotClosed,
    editableNameOnBlur,
    concealTabHeader,
  } = props;
  const [internalTabs, setInternalTabs] = useState<ITabItem[]>([]);
  const [internalActiveTab, setInternalActiveTab] = useState<number | string | null>(null);
  const [editingTab, setEditingTab] = useState<ITabItem['key'] | undefined>();
  const tabListBoxRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (activeKey !== null && activeKey !== undefined) {
      setInternalActiveTab(activeKey);
    }
  }, [activeKey]);

  useEffect(() => {
    setInternalTabs(items || []);
    if (items?.length && (internalActiveTab === undefined || internalActiveTab === null)) {
      setInternalActiveTab(items[0]?.key);
    }
  }, [items]);

  useEffect(() => {
    // 聚焦的时候，聚焦的tab要在第一个
    if (tabListBoxRef.current) {
      const activeTab = tabListBoxRef.current.querySelector(`.${styles.activeTab}`);
      if (activeTab) {
        activeTab.scrollIntoView({ block: 'nearest' });
      }
    }

    onChange?.(internalActiveTab);
  }, [internalActiveTab]);

  function deleteTab(data: ITabItem) {
    const newInternalTabs = internalTabs?.filter((t) => t.key !== data.key);
    let activeKeyTemp = internalActiveTab;
    // 删掉的是当前激活的tab，那么就切换到前一个,如果前一个没有就切换到后一个
    if (data.key === internalActiveTab) {
      const index = internalTabs.findIndex((t) => t.key === data.key);
      if (index === 0) {
        activeKeyTemp = internalTabs[1]?.key;
      } else {
        activeKeyTemp = internalTabs[index - 1]?.key;
      }
    }
    changeTab(activeKeyTemp);
    setInternalTabs(newInternalTabs);
    onEdit?.('remove', data, newInternalTabs);
  }

  function changeTab(key: string | number | null) {
    setInternalActiveTab(key);
  }

  function handleAdd() {
    onEdit?.('add');
  }

  function onDoubleClick(t: ITabItem) {
    if (t.editableName) {
      setEditingTab(t.key);
    }
  }

  function renderTabItem(t: ITabItem, index: number) {
    function inputOnChange(value: string) {
      internalTabs[index].label = value;
      setInternalTabs([...internalTabs]);
    }

    function onBlur() {
      editableNameOnBlur?.(t);
      setEditingTab(undefined);
    }

    function showClosed() {
      if (lastTabCannotClosed && internalTabs.length === 1) {
        return false;
      }
      if (t.canClosed === true) {
        return false;
      }
      return true;
    }

    return (
      <Popover mouseEnterDelay={0.8} content={t.popover} key={t.key}>
        <div
          onDoubleClick={() => {
            onDoubleClick(t);
          }}
          style={t.styles}
          className={classnames(
            styles.tabItem,
            { [styles.activeTab]: t.key === internalActiveTab },
          )}
        >
          {t.key === editingTab ? (
            <input
              value={t.label as string}
              onChange={(e) => {
                inputOnChange(e.target.value);
              }}
              className={styles.input}
              autoFocus
              onBlur={onBlur}
              type="text"
            />
          ) : (
            <div className={styles.textBox} key={t.key} onClick={changeTab.bind(null, t.key)}>
              {t.prefixIcon &&
                (typeof t.prefixIcon == 'string' ? (
                  <Iconfont className={styles.prefixIcon} code={t.prefixIcon} />
                ) : (
                  t.prefixIcon
                ))}
              <div className={styles.text}>{t.label}</div>
            </div>
          )}
          {showClosed() && (
            <div className={styles.icon} onClick={deleteTab.bind(null, t)}>
              <Iconfont code="&#xe634;" />
            </div>
          )}
        </div>
      </Popover>
    );
  }

  return (
    <div className={classnames(styles.tabBox, className)}>
      {!concealTabHeader && (
        <div className={styles.tabsNav}>
          {!!internalTabs?.length && (
            <div className={styles.tabList} ref={tabListBoxRef}>
              {internalTabs.map((t, index) => {
                return renderTabItem(t, index);
              })}
            </div>
          )}
          {!hideAdd && (
            <div className={styles.rightBox}>
              <div className={styles.addIcon} onClick={handleAdd}>
                <Iconfont code="&#xe631;" />
              </div>
            </div>
          )}
        </div>
      )}
      <div className={styles.tabsContent}>
        {internalTabs?.map((t) => {
          return (
            <div
              key={t.key}
              className={classnames(styles.tabsContentItem, {
                [styles.tabsContentItemActive]: t.key === internalActiveTab,
              })}
            >
              {t.children}
            </div>
          );
        })}
      </div>
    </div>
  );
});
