import React, { memo } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import Iconfont from '@/components/Iconfont';
import ScrollLoading from '@/components/ScrollLoading';
import historyService, { IHistoryRecord } from '@/service/history';
import * as monaco from 'monaco-editor';
import i18n from '@/i18n';

interface IProps {
  className?: string;
  curWorkspaceParams: any;
}

interface IDatasource extends IHistoryRecord {
  highlightedCode: string; // sql处理过的高亮代码
}

export default memo<IProps>((props) => {
  const { className } = props;
  const [dataSource, setDataSource] = React.useState<IDatasource[]>([]);
  const outputContentRef = React.useRef<HTMLDivElement>(null);
  const curPageRef = React.useRef(1);
  const finishedRef = React.useRef(false);
  
  const getHistoryList = () => {
    return historyService
      .getHistoryList({
        dataSourceId:props.curWorkspaceParams.dataSourceId,
        pageNo: curPageRef.current++,
        pageSize: 40,
      })
      .then((res) => {
        finishedRef.current = !res.hasNextPage;
        const promiseList = res.data.map((item) => {
          return new Promise((resolve) => {
            // 不换行
            const ddl = (item.ddl || '')?.replace(/\n/g, ' ');
            monaco.editor.colorize(ddl, 'sql', {}).then((_res) => {
              resolve({
                ...item,
                highlightedCode: _res,
              });
            });
          });
        });
        Promise.all(promiseList).then((_res) => {
          setDataSource([...dataSource, ..._res] as any);
        });
      });
  };

  return (
    <div className={classnames(styles.output, className)}>
      <div className={styles.outputTitle}>
        <Iconfont code="&#xe8ad;" />
        {i18n('common.title.executiveLogging')}
      </div>
      <div className={styles.outputContent} ref={outputContentRef}>
        <ScrollLoading
          onReachBottom={getHistoryList}
          scrollerElement={outputContentRef}
          threshold={300}
          finished={finishedRef.current}
        >
          <>
            {dataSource.map((item, index) => {
              const nameList = [item.databaseName, item.schemaName];
              return (
                <div key={index} className={styles.outputItem}>
                  <div className={styles.timeBox}>
                    <div className={classnames(styles.iconBox, { [styles.failureIconBox]: item.status !== 'success' })}>
                      <Iconfont code="&#xe650;" />
                    </div>
                    <span className={styles.timeSpan}>[{item.gmtCreate}]</span>
                    {/* {!!item.operationRows && <span>{item.operationRows} rows</span>} */}
                    {!!item.useTime && <span>{i18n('common.text.executionTime',item.useTime)}</span>}
                  </div>
                  <div className={styles.sqlPlace}>{nameList.filter((name) => name).join(' > ')}</div>
                  <div className={styles.sqlBox} dangerouslySetInnerHTML={{ __html: item.highlightedCode }} />
                </div>
              );
            })}
          </>
        </ScrollLoading>
      </div>
    </div>
  );
});
