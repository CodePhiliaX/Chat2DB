import React, { memo, useEffect, useRef, useState } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { IConnectionDetails, IDatabase } from '@/typings';
import ConnectionEdit, { ICreateConnectionFunction } from '@/components/ConnectionEdit';
import { databaseTypeList } from '@/constants';
import Iconfont from '@/components/Iconfont';

interface IProps {
  className?: string;
  onSubmit?: (data: IConnectionDetails) => void; // 点击保存或修改的回调，我会把数据给你
  connectionDetail?: IConnectionDetails; // 如果你想编辑，就直接传入完成的数据就好
}

export default memo<IProps>((props) => {
  const { className, onSubmit, connectionDetail } = props;
  const [curConnection, setCurConnection] = useState<Partial<IConnectionDetails>>({});
  const createConnectionRef = useRef<ICreateConnectionFunction>();

  useEffect(() => {
    if (connectionDetail) {
      setCurConnection(connectionDetail);
    } else {
      setCurConnection({});
    }
  }, [connectionDetail]);

  function handleCreateConnections(database: IDatabase) {
    setCurConnection({
      type: database.code,
    });
  }

  function handleSubmit(data: IConnectionDetails) {
    onSubmit?.(data);
  }

  return (
    <div className={classnames(styles.box, className)}>
      {curConnection && Object.keys(curConnection).length ? (
        <div
          className={classnames(styles.createConnections, {
            [styles.showCreateConnections]: Object.keys(curConnection).length,
          })}
        >
          {
            <ConnectionEdit
              ref={createConnectionRef as any}
              closeCreateConnection={() => {
                setCurConnection({});
              }}
              connectionData={curConnection as any}
              submit={handleSubmit}
            />
          }
        </div>
      ) : (
        <div className={styles.dataBaseList}>
          {databaseTypeList.map((t) => {
            return (
              <div key={t.code} className={styles.databaseItem} onClick={handleCreateConnections.bind(null, t)}>
                <div className={styles.databaseItemMain}>
                  <div className={styles.databaseItemLeft}>
                    <div className={styles.logoBox}>
                      <Iconfont code={t.icon} />
                    </div>
                    {t.name}
                  </div>
                  <div className={styles.databaseItemRight}>
                    <Iconfont code="&#xe631;" />
                  </div>
                </div>
              </div>
            );
          })}
          {Array.from({ length: 20 }).map((t, index) => {
            return <div key={index} className={styles.databaseItemSpacer} />;
          })}
        </div>
      )}
    </div>
  );
});
