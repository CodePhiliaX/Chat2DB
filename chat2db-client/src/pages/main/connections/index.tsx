import React, { Fragment, memo, useEffect, useMemo, useRef, useState } from 'react';
import cs from 'classnames';
import i18n from '@/i18n';
import CreateConnection from '@/components/CreateConnection';
import Iconfont from '@/components/Iconfont';
import connectionService from '@/service/connection';

import { DatabaseTypeCode, databaseMap, databaseTypeList } from '@/constants/database';

import { IDatabase } from '@/typings/database';
import { IConnectionDetails } from '@/typings/connection';
import { Button, Dropdown, Modal } from 'antd';
import { MoreOutlined } from '@ant-design/icons';
import styles from './index.less';

interface IMenu {
  key: number;
  label: string;
  icon: React.ReactNode;
  meta: IConnectionDetails;
}
interface IProps {}

export default memo<IProps>(function Connections(props) {
  const volatileRef = useRef<any>();
  const [connectionList, setConnectionList] = useState<IConnectionDetails[]>();
  const [curConnection, setCurConnection] = useState<Partial<IConnectionDetails>>({});

  useEffect(() => {
    getConnectionList();
  }, []);

  function getConnectionList() {
    let p = {
      pageNo: 1,
      pageSize: 999,
    };
    connectionService.getList(p).then((res) => {
      setConnectionList(res.data);
    });
  }

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
              className={styles.menuItems}
              onClick={() => {
                setCurConnection(menu.meta);
              }}
            >
              <div
                className={cs(styles.menuItemsTitle, {
                  [styles.menuItemActive]: curConnection.id === key,
                })}
              >
                {icon}
                <span style={{ marginLeft: '8px' }}>{label}</span>
              </div>
              <Dropdown
                menu={{
                  items: [
                    {
                      key: 'Delete',
                      label: i18n('common.button.delete'),
                      onClick: async ({ domEvent }) => {
                        domEvent.preventDefault();
                        await connectionService.remove({ id: key });
                        // if (key === curConnection.id) {
                        // }
                        setCurConnection({});
                        getConnectionList();
                      },
                    },
                  ],
                }}
              >
                <MoreOutlined />
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
        {/* <div className={styles.menuBox}>
          <Menu
            className={styles.menu}
            mode="inline"
            items={menuItems}
            onClick={changeMenu}
            selectedKeys={[checkedConnection!]}
          ></Menu>
        </div> */}
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
            className={cs(styles.createConnections, {
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
            <div className={styles.databaseItemSpacer}></div>
            <div className={styles.databaseItemSpacer}></div>
          </div>
        )}
      </div>
    </div>
  );
});
