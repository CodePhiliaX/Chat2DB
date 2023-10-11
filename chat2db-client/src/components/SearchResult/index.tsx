import React, { memo, useCallback, useEffect, useMemo, useState } from 'react';
import classnames from 'classnames';
import TabsNew from '@/components/TabsNew';
import Iconfont from '@/components/Iconfont';
import StateIndicator from '@/components/StateIndicator';
import { IManageResultData, IResultConfig } from '@/typings';
import TableBox from './TableBox';
import styles from './index.less';

interface IProps {
  className?: string;
  manageResultDataList?: IManageResultData[];
  resultConfig: IResultConfig[];
  onExecute: (sql: string, config: IResultConfig, index: number) => void;
  onTabEdit: (type: 'add' | 'remove', value?: number | string) => void;
  onSearchTotal: (index: number) => Promise<number>;
  executeSqlParams: any;
}

export default memo<IProps>((props) => {
  const { className, manageResultDataList = [], onExecute } = props;
  const [currentTab, setCurrentTab] = useState<string | number | undefined>();
  const [resultDataList, setResultDataList] = useState<IManageResultData[]>([]);
  const [resultConfig, setResultConfig] = useState<IResultConfig[]>([]);

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
  }, [manageResultDataList]);

  const onChange = useCallback((uuid: string | number) => {
    setCurrentTab(uuid);
  }, []);

  const onEdit = useCallback((type: 'add' | 'remove', value?: number | string) => {
    props.onTabEdit && props.onTabEdit(type, value);
  }, []);

  const renderTable = (item, index) => {
    if (item.success) {
      return (
        <TableBox
          key={item.uuid}
          data={item}
          config={resultConfig?.[index]}
          executeSqlParams={props.executeSqlParams}
          onConfigChange={(config: IResultConfig) => {
            onExecute && onExecute(item.originalSql, config, index);
          }}
          onSearchTotal={async () => {
            if (props.onSearchTotal) {
              return await props.onSearchTotal(index);
            }
          }}
        />
      );
    } else {
      return <StateIndicator key={item.uuid} state="error" text={item.message} />;
    }
  };

  const tabsList = useMemo(() => {
    return resultDataList.map((item, index) => {
      return {
        prefixIcon: (
          <Iconfont
            key={index}
            className={classnames(styles[item.success ? 'successIcon' : 'failIcon'], styles.statusIcon)}
            code={item.success ? '\ue605' : '\ue87c'}
          />
        ),
        popover: item.originalSql,
        label: item.originalSql,
        key: item.uuid!,
        children: renderTable(item, index),
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
          onEdit={onEdit as any}
          activeKey={currentTab}
          // items={[outputTab, ...tabsList]}
          items={[...tabsList]}
        />
      )}
    </div>
  );
});
