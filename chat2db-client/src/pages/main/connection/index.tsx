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
import { connect, history, Dispatch } from 'umi';
import { IConnectionModelType } from '@/models/connection';
import { IWorkspaceModelType } from '@/models/workspace';

interface IMenu {
  key: number;
  label: string;
  icon: React.ReactNode;
  meta: IConnectionDetails;
}

interface IProps {
  connectionModel: IConnectionModelType['state'];
  workspaceModel: IWorkspaceModelType['state'];
  dispatch: any;
}

function Connections(props: IProps) {
  const { connectionModel, workspaceModel, dispatch } = props;
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

  const handleMenuItemDoubleClick = (t?: any) => {
    dispatch({
      type: 'connection/setCurConnection',
      payload: t.meta,
    });

    dispatch({
      type: 'mainPage/updateCurPage',
      payload: 'workspace',
    });
  };

  const renderMenu = () => {
    return (
      <div className={styles.menuBox}>
        {(menuItems || []).map((t) => {
          const { key, label, icon } = t;
          return (
            <div
              key={key}
              className={classnames(styles.menuItem, {
                [styles.menuItemActive]: curConnection.id === key,
              })}
              onDoubleClick={handleMenuItemDoubleClick.bind(null, t)}
              onClick={() => {
                setCurConnection(t.meta);
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
                        handleMenuItemDoubleClick(t);
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
              connectionData={curConnection as any}
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
            {Array.from({ length: 20 }).map((t, index) => {
              return <div key={index} className={styles.databaseItemSpacer}></div>;
            })}
          </div>
        )}
      </div>
    </div>
  );
}

export default connect(
  ({ connection, workspace }: { connection: IConnectionModelType; workspace: IWorkspaceModelType }) => ({
    connectionModel: connection,
    workspaceModel: workspace,
  }),
)(Connections);
