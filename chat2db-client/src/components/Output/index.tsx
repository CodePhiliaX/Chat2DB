import React, { memo } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import Iconfont from '@/components/Iconfont';
import ScrollLoading from '@/components/ScrollLoading';
import historyService, { IHistoryRecord } from '@/service/history';
import * as monaco from 'monaco-editor';
import i18n from '@/i18n';
import { copy } from '@/utils';
import { createConsole } from '@/pages/main/workspace/store/console';
import { Popover } from 'antd';

interface IProps {
  className?: string;
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
        // dataSourceId:props.curWorkspaceParams.dataSourceId,
        pageNo: curPageRef.current++,
        pageSize: 40,
      })
      .then((res) => {
        finishedRef.current = !res.hasNextPage;
        const promiseList = res.data.map((item) => {
          return new Promise((resolve) => {
            // 不换行
            // const ddl = (item.ddl || '')?.replace(/\n/g, ' ');
            const ddl = item.ddl || '';
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

  const copySql = (text: IDatasource['ddl']) => {
    copy(text || '');
  }

  const openSql = (data: IDatasource) => {
    createConsole({
      ddl: data.ddl || '',
      dataSourceId: data.dataSourceId!,
      dataSourceName: data.dataSourceName!,
      databaseType: data.type!,
      databaseName: data.databaseName!,
      schemaName: data.schemaName!,
      // operationType: WorkspaceTabType,
    })
  }

  return (
    <div className={classnames(styles.output, className)}>
      <div className={styles.outputTitle}>
        {/* <Iconfont code="&#xe8ad;" /> */}
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
              const nameList = [item.dataSourceName, item.databaseName, item.schemaName];
              return (
                <div key={index} className={styles.outputItem}>
                  <div className={styles.timeBox}>
                    <Iconfont classNameBox={classnames(styles.timeBoxIcon, { [styles.failureIconBox]: item.status !== 'success' })} box boxSize={20} code="&#xe650;" />
                    <span className={styles.timeSpan}>[{item.gmtCreate}]</span>
                    {/* {!!item.operationRows && <span>{item.operationRows} rows</span>} */}
                    {!!item.useTime && <span>{i18n('common.text.executionTime', item.useTime)}</span>}
                  </div>
                  <div className={styles.executedDatabaseBox}>
                    <Popover mouseEnterDelay={0.8} content={i18n('workspace.tips.openExecutiveLogging')}>
                      <Iconfont classNameBox={styles.iconBox} box boxSize={20} code="&#xe6bb;" onClick={()=>{openSql(item)}} />
                    </Popover>
                    <div className={styles.executedDatabase}>
                      {nameList.filter((name) => name).join(' > ')}
                    </div>
                  </div>
                  <div className={styles.sqlBox}>
                    <Popover mouseEnterDelay={0.8} content={i18n('common.button.copy')}>
                      <Iconfont classNameBox={styles.iconBox} box boxSize={20} code="&#xec7a;" onClick={()=>{copySql(item.ddl)}} />
                    </Popover>
                    <div className={styles.sqlContent} dangerouslySetInnerHTML={{ __html: item.highlightedCode }} />
                  </div>
                </div>
              );
            })}
          </>
        </ScrollLoading>
      </div>
    </div>
  );
});
