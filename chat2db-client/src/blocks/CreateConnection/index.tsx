import React, { memo, useEffect, useState } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { IConnectionDetails, IDatabase } from '@/typings';
import ConnectionEdit from '@/components/ConnectionEdit';
import { databaseTypeList } from '@/constants';
import Iconfont from '@/components/Iconfont';

// IConnectionDetails 全部信息代表修改
// null 展示因增列表
//  { type: string } 只有数据库类型代表新增
type IEditConnectionDetail = IConnectionDetails | null | Pick<IConnectionDetails, 'type'>;

interface IProps {
  className?: string;
  onSubmit?: (data: IConnectionDetails) => Promise<any>; // 点击保存或修改的回调，我会把数据给你
  connectionDetail: IEditConnectionDetail | null | undefined;
  noPermission?: boolean;
}

export default memo<IProps>((props) => {
  const { className, onSubmit, connectionDetail: externalConnectionDetail } = props;
  const [connectionDetail, setConnectionDetail] = useState<IEditConnectionDetail | null | undefined>(
    externalConnectionDetail,
  );

  useEffect(() => {
    setConnectionDetail(externalConnectionDetail);
  }, [externalConnectionDetail]);

  function handleCreateConnections(database: IDatabase) {
    setConnectionDetail({
      type: database.code,
    });
  }

  // function handleSubmit(data: IConnectionDetails) {
  //   return onSubmit?.(data);
  // }

  return (
    <div className={classnames(styles.box, className)}>
      {connectionDetail && (
        <div
          className={classnames(styles.createConnections, {
            [styles.showCreateConnections]: connectionDetail,
          })}
        >
          <ConnectionEdit
            closeCreateConnection={() => {
              setConnectionDetail(null);
            }}
            connectionData={connectionDetail as any}
            submit={onSubmit}
          />
        </div>
      )}
      {connectionDetail === null && (
        <div className={styles.dataBaseListBox}>
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
        </div>
      )}
    </div>
  );
});

{
  /* <div className={styles.notPermission}>
  <div className={styles.notPermissionIconBox}>
    <Iconfont className={styles.notPermissionIcon} code="&#xe658;" />
  </div>
  <div className={styles.notPermissionIconTips}>{i18n('connection.tips.noConnectionTips')}</div>
  <div className={styles.connectButtonBox}>
    <Button
      type="primary"
      className={styles.connectButton}
      icon={<Iconfont code="&#xec57;" />}
      onClick={() => {
        handleMenuItemDoubleClick(curConnection);
      }}
    >
      {i18n('connection.button.connect')}
    </Button>
  </div>
</div> */
}
