import React, { memo, useCallback, useEffect, useMemo, useState } from 'react';
import classnames from 'classnames';
import TabsNew from '@/components/TabsNew';
import Iconfont from '@/components/Iconfont';
import StateIndicator from '@/components/StateIndicator';
import { IManageResultData } from '@/typings';
import TableBox from './TableBox';
import styles from './index.less';

interface IProps {
  className?: string;
  queryResultDataList?: IManageResultData[];
  executeSqlParams: any;
}

export default memo<IProps>((props) => {
  const { className, queryResultDataList = [] } = props;
  const [currentTab, setCurrentTab] = useState<string | number | undefined>();
  const [resultDataList, setResultDataList] = useState<IManageResultData[]>([]);

  useEffect(() => {
    if (!queryResultDataList.length) {
      return;
    }

    if (!currentTab || !queryResultDataList.find((d) => d.uuid === currentTab)) {
      setCurrentTab(queryResultDataList[0].uuid);
    }

    setResultDataList([...queryResultDataList]);
  }, [queryResultDataList]);

  const onChange = useCallback((uuid: string | number) => {
    setCurrentTab(uuid);
  }, []);

  const renderTable = (queryResultData) => {
    if (queryResultData.success) {
      return (
        <TableBox
          key={queryResultData.uuid}
          outerQueryResultData={queryResultData}
          executeSqlParams={props.executeSqlParams}
        />
      );
    } else {
      return <StateIndicator key={queryResultData.uuid} state="error" text={queryResultData.message} />;
    }
  };

  const tabsList = useMemo(() => {
    return resultDataList.map((queryResultData, index) => {
      return {
        prefixIcon: (
          <Iconfont
            key={index}
            className={classnames(styles[queryResultData.success ? 'successIcon' : 'failIcon'], styles.statusIcon)}
            code={queryResultData.success ? '\ue605' : '\ue87c'}
          />
        ),
        popover: queryResultData.originalSql,
        label: queryResultData.originalSql,
        key: queryResultData.uuid!,
        children: renderTable(queryResultData),
      };
    });
  }, [resultDataList]);

  const outputTab = useMemo(() => {
    return {
      prefixIcon: (
        <Iconfont key="output" className={classnames(styles['successIcon'], styles.statusIcon)} code={'\ue605'} />
      ),
      popover: 'output',
      label: 'output',
      key: 'output',
      children: <div>output</div>,
    };
  }, []);

  return (
    <div className={classnames(className, styles.searchResult)}>
      {!!tabsList.length && (
        <TabsNew
          hideAdd
          className={styles.tabs}
          onChange={onChange as any}
          activeKey={currentTab}
          // items={[outputTab, ...tabsList]}
          items={[...tabsList]}
        />
      )}
    </div>
  );
});
