import React, { Fragment, memo, useEffect, useMemo, useRef, useState } from 'react';
import classnames from 'classnames';
import i18n from '@/i18n';
import CreateConnection from '@/components/CreateConnection';
import Iconfont from '@/components/Iconfont';
import connectionService from '@/service/connection';

import { DatabaseTypeCode, databaseMap, databaseTypeList } from '@/constants';

import { IDatabase, IConnectionDetails } from '@/typings';
import { Button, Dropdown, Modal } from 'antd';
import { MoreOutlined } from '@ant-design/icons';
import styles from './index.less';
import { connect, history } from 'umi';
import { ConnectionModelType } from '@/models/connection';

interface IMenu {
  key: number;
  label: string;
  icon: React.ReactNode;
  meta: IConnectionDetails;
}

interface IProps {
  connectionModel: {
    connectionList: IConnectionDetails[];
    curConnection: IConnectionDetails;
  };
  databaseModel: {
    databaseAndSchemaList: any;
  };
  dispatch: (p: { type: `connection/${string}` | `database/${string}`; payload ?: any }) => void;
}

function Connections(props: IProps) {
  const { connectionModel, databaseModel, dispatch } = props;
  const { connectionList } = connectionModel;
  const volatileRef = useRef<any>();
  // const [connectionList, setConnectionList] = useState<IConnectionDetails[]>();
  const [curConnection, setCurConnection] = useState<Partial<IConnectionDetails>>({});

  useEffect(() => {
    getConnectionList();
  }, []);

  const getConnectionList = async () => {
    dispatch({
      type: 'connection/fetchConnectionList',
    });
  };

  function handleCreateConnections(database: IDatabase) {
    setCurConnection({
      type: database.code,
    });
  }

  const menuItems: IMenu[] = useMemo(
    () =>
      (connectionList || []).map((t) => ({
        key: t.id,
        icon: <Iconfont className={styles.menuItemIcon} code={databaseMap[t.type].icon} />,
        label: t.alias,
        meta: t,
      })),
    [connectionList],
  );

  const renderMenu = () => {
    return (
      <div className={styles.menuBox}>
        {(menuItems || []).map((menu) => {
          const { key, label, icon } = menu;
          return (
            <div
              key={key}
              className={classnames(styles.menuItem, {
                [styles.menuItemActive]: curConnection.id === key,
              })}
              onClick={() => {
                setCurConnection(menu.meta);
              }}
            >
              <div className={classnames(styles.menuItemsTitle)}>
                {icon}
                <span style={{ marginLeft: '8px' }}>{label}</span>
              </div>
              <Dropdown
                menu={{
                  items: [
                    {
                      key: 'EnterWorkSpace',
                      label: i18n('connection.button.connect'),
                      onClick: () => {
                        // dispatch({
                        //   type: 'connection/setCurConnection',
                        //   payload: menu.meta,
                        // });

                        dispatch({
                          type: 'mainPage/updateCurPage',
                          payload: 'workspace'
                        })

                        dispatch({
                          type: 'database/fetchDatabaseAndSchemaList',
                        });
                      },
                    },
                    {
                      key: 'Delete',
                      label: i18n('common.button.delete'),
                      onClick: async ({ domEvent }) => {
                        domEvent.preventDefault();
                        await connectionService.remove({ id: key });
                        setCurConnection({});
                        getConnectionList();
                      },
                    },
                  ],
                }}
              >
                <div className={styles.moreButton}>
                  <Iconfont code="&#xe601;"></Iconfont>
                </div>
              </Dropdown>
            </div>
          );
        })}
      </div>
    );
  };

  return (
    <div className={styles.box}>
      <div ref={volatileRef} className={styles.layoutLeft}>
        <div className={styles.pageTitle}>{i18n('connection.title.connections')}</div>
        {renderMenu()}
        <Button
          type="primary"
          className={styles.addConnection}
          onClick={() => {
            setCurConnection({});
          }}
        >
          {i18n('connection.button.addConnection')}
        </Button>
      </div>
      <div className={styles.layoutRight}>
        {curConnection && Object.keys(curConnection).length ? (
          <div
            className={classnames(styles.createConnections, {
              [styles.showCreateConnections]: Object.keys(curConnection).length,
            })}
          >
            <CreateConnection
              connectionData={curConnection}
              closeCreateConnection={() => {
                setCurConnection({});
              }}
              submitCallback={getConnectionList}
            />
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
              {Array.from({ length: 20 }).map((t) => {
                return <div className={styles.databaseItemSpacer}></div>;
              })}
            </div>
          )}
      </div>
    </div>
  );
}

export default connect(({ connection, database }: { connection: ConnectionModelType; database: any }) => ({
  connectionModel: connection,
  databaseModel: database,
}))(Connections);
