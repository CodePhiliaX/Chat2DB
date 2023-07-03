import React, { memo, useEffect, useState, useRef, useMemo, Fragment } from 'react';
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
      value: item.uuid!,
    };
  });
};

export default memo<IProps>(function SearchResult({ className, manageResultDataList = [] }) {
  const [isUnfold, setIsUnfold] = useState(true);
  const [currentTab, setCurrentTab] = useState<string | number | undefined>();
  const [resultDataList, setResultDataList] = useState<IManageResultData[]>([]);
  const [tabs, setTabs] = useState<IOption[]>([]);

  useEffect(() => {
    if (!manageResultDataList.length) {
      return
    }
    const newManageResultDataList = manageResultDataList.map(t => {
      return {
        ...t,
        uuid: uuidv4()
      }
    })
    setCurrentTab(newManageResultDataList[0].uuid)
    setResultDataList(newManageResultDataList);
    setTabs(handleTabs(newManageResultDataList));
  }, [manageResultDataList]);

  function onChange(uuid: string | number) {
    setCurrentTab(uuid);
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
      const dataList = resultDataList.filter((t) => t.uuid !== value);
      setResultDataList(dataList);
      if (currentTab === value) {
        setCurrentTab(dataList[0]?.uuid);
      }
    }
  }

  const renderEmpty = () => {
    return <div className={styles.noData}>{i18n('common.text.noData')}</div >;
  };

  const renderTable = useMemo(() => {
    if (!tabs || !tabs.length) {
      return renderEmpty();
    }
    if (!resultDataList || !resultDataList.length) {
      return renderEmpty();
    }
    return (resultDataList || []).map((item, index: number) => {
      if (item.success) {
        return (
          <Fragment key={item.uuid!}>
            <TableBox
              className={classnames({ [styles.cursorTableBox]: item.uuid === currentTab })}
              data={item}
            />
          </Fragment>
        );
      } else {
        return <Fragment key={item.uuid} >
          <StateIndicator className={classnames(styles.stateIndicator, { [styles.cursorStateIndicator]: item.uuid === currentTab })} state="error" text={item.message} />
        </Fragment >;
      }
    });
  }, [currentTab])

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
      <div className={styles.resultContent}>{renderTable}</div>
    </div>
  );
});
