import React, { memo, useEffect, useState, useMemo, Fragment, useCallback } from 'react';
import classnames from 'classnames';
import Tabs, { IOption } from '@/components/Tabs';
import Iconfont from '@/components/Iconfont';
import StateIndicator from '@/components/StateIndicator';
import { Spin, Popover } from 'antd';
import { IManageResultData, IResultConfig } from '@/typings';
import i18n from '@/i18n';
import TableBox from './TableBox';
import EmptyImg from '@/assets/img/empty.svg';
import { ExportSizeEnum, ExportTypeEnum } from '@/typings/resultTable';
import styles from './index.less';

interface IProps {
  className?: string;
  manageResultDataList?: IManageResultData[];
  resultConfig: IResultConfig[];
  onExecute: (sql: string, config: IResultConfig, index: number) => void;
  onExport: (sql: string, originalSql: string, exportType: ExportTypeEnum, exportSize: ExportSizeEnum) => Promise<void>;
  onTabEdit: (type: 'add' | 'remove', value?: number | string) => void;
  onSearchTotal: (index: number) => Promise<number>;
  isLoading?: boolean;
}

interface DataType {
  [key: string]: any;
}

const handleTabs = (result: IManageResultData[]) => {
  return (result || []).map((item, index) => {
    return {
      label: (
        <Popover content={item.originalSql}>
          <Iconfont
            key={index}
            className={classnames(styles[item.success ? 'successIcon' : 'failIcon'], styles.statusIcon)}
            code={item.success ? '\ue605' : '\ue87c'}
          />
          {`${i18n('common.text.executionResult')}-${index + 1}`}
        </Popover>
      ),
      value: item.uuid!,
    };
  });
};

export default memo<IProps>(function SearchResult(props) {
  const { className, manageResultDataList = [], isLoading, onExecute, onExport } = props;
  const [currentTab, setCurrentTab] = useState<string | number | undefined>();
  const [resultDataList, setResultDataList] = useState<IManageResultData[]>([]);
  const [resultConfig, setResultConfig] = useState<IResultConfig[]>([]);
  const [tabs, setTabs] = useState<IOption[]>([]);

  useEffect(() => {
    setResultConfig(props.resultConfig);
  }, [props.resultConfig]);

  useEffect(() => {
    if (!manageResultDataList.length) {
      return;
    }

    if (!currentTab || !manageResultDataList.find((d) => d.uuid === currentTab)) {
      setCurrentTab(manageResultDataList[0].uuid);
    }

    setResultDataList([...manageResultDataList]);
    setTabs(handleTabs(manageResultDataList));
  }, [manageResultDataList]);

  function onChange(uuid: string | number) {
    setCurrentTab(uuid);
  }

  function onEdit(type: 'add' | 'remove', value?: number | string) {
    props.onTabEdit && props.onTabEdit(type, value);
  }

  const renderEmpty = () => {
    return (
      <div className={styles.noData}>
        <img src={EmptyImg} />
        <p>{i18n('common.text.noData')}</p>
      </div>
    );
  };

  const renderTable = () => {
    if (!tabs || !tabs.length) {
      return renderEmpty();
    }
    if (!resultDataList || !resultDataList.length) {
      return renderEmpty();
    }

    return renderTableContent;
  };

  const renderTableContent = (resultDataList || []).map((item, index: number) => {
    console.log(item.sql, item.originalSql);
    if (item.success) {
      return (
        <TableBox
          key={item.uuid!}
          className={classnames({ [styles.cursorTableBox]: item.uuid === currentTab })}
          data={item}
          config={resultConfig?.[index]}
          onConfigChange={(config: IResultConfig) => {
            onExecute && onExecute(item.originalSql, config, index);
          }}
          onSearchTotal={async () => {
            if (props.onSearchTotal) {
              return await props.onSearchTotal(index);
            }
          }}
          onExport={(sql: string, originalSql: string, exportType: ExportTypeEnum, exportSize: ExportSizeEnum) => {
            onExport && onExport(sql, originalSql, exportType, exportSize);
          }}
        />
      );
    } else {
      return (
        <StateIndicator
          key={item.uuid}
          className={classnames(styles.stateIndicator, { [styles.cursorStateIndicator]: item.uuid === currentTab })}
          state="error"
          text={item.message}
        />
      );
    }
  });

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
      <Spin spinning={isLoading} wrapperClassName={styles.resultContentWrapper}>
        {renderTable()}
      </Spin>
    </div>
  );
});
