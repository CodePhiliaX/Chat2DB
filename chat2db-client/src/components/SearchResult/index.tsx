import React, { memo, useEffect, useState, useRef, useMemo } from 'react';
import classnames from 'classnames';
import Tabs, { IOption } from '@/components/Tabs';
import Iconfont from '@/components/Iconfont';
import StateIndicator from '@/components/StateIndicator';
import LoadingContent from '@/components/Loading/LoadingContent';
import MonacoEditor from '@/components/Console/MonacoEditor';
import { Button, DatePicker, Input, Table, Modal, message } from 'antd';
import { StatusType, TableDataType } from '@/constants';
import { formatDate } from '@/utils/date';
import { IManageResultData, ITableHeaderItem } from '@/typings';
import styles from './index.less';
import i18n from '@/i18n';
import { v4 as uuidv4 } from 'uuid';
import TableBox from './TableBox';

interface IProps {
  className?: string;
  manageResultDataList?: IManageResultData[];
}

interface DataType {
  [key: string]: any;
}

const handleTabs = (result: IManageResultData[]) => {
  return (result || []).map((item, index) => {
    return {
      label: (
        <>
          <Iconfont
            key={index}
            className={classnames(styles[item.success ? 'successIcon' : 'failIcon'], styles.statusIcon)}
            code={item.success ? '\ue605' : '\ue87c'}
          />
          {`${i18n('common.text.executionResult')}-${index + 1}`}
        </>
      ),
      value: index,
    };
  });
};

export default memo<IProps>(function SearchResult({ className, manageResultDataList = [] }) {
  const [isUnfold, setIsUnfold] = useState(true);
  const [currentTab, setCurrentTab] = useState<string | number>(0);
  const [resultDataList, setResultDataList] = useState<any>([]);
  const [tabs, setTabs] = useState<IOption[]>([]);

  useEffect(() => {
    setResultDataList(manageResultDataList);
    setTabs(handleTabs(manageResultDataList));
  }, [manageResultDataList]);

  function onChange(index: string | number) {
    setCurrentTab(index);
  }

  const renderStatus = (text: string) => {
    return (
      <div className={styles.tableStatus}>
        <i className={classnames(styles.dot, { [styles.successDot]: text == StatusType.SUCCESS })}></i>
        {text == StatusType.SUCCESS ? '成功' : '失败'}
      </div>
    );
  };

  function onEdit(type: 'add' | 'remove', value?: number | string) {
    if (type === 'remove') {
      if (currentTab === value) {
        setCurrentTab(0);
      }
      const remainTabs = tabs.filter((t) => t.value !== value);
      setTabs(remainTabs);

      const dataList = resultDataList.filter((t) => t.uuid !== value);
      // resultDataList.splice(value as number, 1);
      setResultDataList(dataList);
    }
  }

  const renderEmpty = () => {
    return <div>暂无数据</div>;
  };

  const renderTable = () => {
    if (!tabs || !tabs.length) {
      return renderEmpty();
    }
    if (!resultDataList || !resultDataList.length) {
      return renderEmpty();
    }

    return (resultDataList || []).map((item: any, index: number) => {
      if (item.success) {
        return (
          <TableBox className={classnames({ [styles.cursorTableBox]: index === currentTab })} key={index} data={item} />
        );
      } else {
        return <StateIndicator key={index} state="error" text={item.message} />;
      }
    });
  };

  return (
    <div className={classnames(className, styles.box)}>
      {tabs.length ? (
        <div className={styles.resultHeader}>
          <Tabs
            hideAdd
            type="line"
            onEdit={onEdit}
            onChange={onChange}
            tabs={tabs}
            className={styles.tabs}
            activeTab={currentTab}
          />
        </div>
      ) : null}
      <div className={styles.resultContent}>{renderTable()}</div>
    </div>
  );
});
