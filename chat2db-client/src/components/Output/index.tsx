import React, { memo, useEffect } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import Iconfont from '@/components/Iconfont';
import historyService, { IHistoryRecord } from '@/service/history';
import MonacoEditor from '@/components/Console/MonacoEditor';
interface IProps {
  className?: string;
}

export default memo<IProps>((props) => {
  const { className } = props;
  const [dataSource, setDataSource] = React.useState<IHistoryRecord[]>([]);

  useEffect(() => {
    getHistoryList();
  }, []);

  const getHistoryList = () => {
    historyService
      .getHistoryList({
        pageNo: 3,
        pageSize: 100,
      })
      .then((res) => {
        setDataSource(res.data);
      });
  };

  return (
    <div className={classnames(styles.output, className)}>
      <div className={styles.outputTitle}>
        <Iconfont code="&#xe8ad;" />
        执行记录
      </div>
      <div className={styles.outputContent}>
        {dataSource.map((item, index) => {
          const nameList = [item.dataSourceName, item.databaseName, item.schemaName];
          return (
            <div key={index} className={styles.outputItem}>
              <div className={styles.timeBox}>
                <div className={styles.iconBox}>
                  <Iconfont code="&#xe650;" />
                </div>
                <span>[2023-10-15 14:50:29]</span>
                {item.operationRows && <span>{item.operationRows} rows</span>}
                {item.useTime && <span>affected in{item.useTime} ms</span>}
              </div>
              <div>{nameList.filter((name) => name).join(' > ')}</div>
              <div className={styles.sqlBox}>
                {item.ddl}
                {/* <MonacoEditor
                  options={{
                    // 不需要行号
                    lineNumbers: 'off',
                    readOnly: true,
                    minimap: {
                      enabled: false,
                    },
                  }}
                  id={`output-content-${index}`}
                  defaultValue={item.ddl || ''}
                /> */}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
});
