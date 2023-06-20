import React, { Fragment, memo, useEffect, useMemo, useRef, useState } from 'react';
import classnames from 'classnames';
import i18n from '@/i18n';

import CreateConnection from '@/components/CreateConnection';
import Iconfont from '@/components/Iconfont';

import connectionService from '@/service/connection';
import { DatabaseTypeCode, databaseMap } from '@/constants/database';
import { databaseTypeList } from '@/constants/database';
import { IDatabase } from '@/typings/database';
import { IConnectionDetails } from '@/typings/connection';

import type { MenuProps } from 'antd';
import { Button, Menu, Dropdown } from 'antd';
import styles from './index.less';

type MenuItem = Required<MenuProps>['items'][number];

function getItem(
  label: React.ReactNode,
  key: React.Key,
  icon?: React.ReactNode,
  children?: MenuItem[],
  type?: 'group',
): MenuItem {
  return {
    key,
    icon,
    children,
    label,
    type,
  } as MenuItem;
}

interface IProps {
}

export default memo<IProps>(function Connections(props) {
  const volatileRef = useRef<any>();
  const [createConnectionType, setCreateConnectionType] = useState<DatabaseTypeCode>();
  const [connectionList, setConnectionList] = useState<IConnectionDetails[]>();
  const [checkedConnection, setCheckedConnection] = useState();

  useEffect(() => {
    getDataSource();
  }, [])

  function getDataSource() {
    let p = {
      pageNo: 1,
      pageSize: 999
    }
    connectionService.getList(p).then(res => {
      setConnectionList(res.data);
    })
  }

  function handleCreateConnections(database: IDatabase) {
    setCreateConnectionType(database.code);
    setCheckedConnection(undefined);
  }

  function changeMenu(e: any) {
    setCheckedConnection(e.key);
    setCreateConnectionType(undefined);
  }

  const menuItems: any = useMemo(() => connectionList?.map((t, i) => {
    return getItem(t.alias, t.id!, <Iconfont className={styles.menuItemIcon} code={databaseMap[t.type].icon} />)
  }), [connectionList]);

  return <div className={classnames(styles.box)}>
    <div ref={volatileRef} className={styles.layoutLeft}>
      <div className={styles.pageTitle}>
        {i18n('connection.title.connections')}
      </div>
      <div className={styles.menuBox}>
        <Menu
          className={styles.menu}
          mode="inline"
          items={menuItems}
          onClick={changeMenu}
          selectedKeys={[checkedConnection!]}
        />
      </div>
      {/* <Button
        type="primary"
        className={styles.addConnection}
        onClick={() => { setCheckedConnection(undefined); setCreateConnectionType(undefined) }}
      >
        {i18n('connection.button.addConnection')}
      </Button> */}
    </div>
    <div className={styles.layoutRight}>
      {
        (createConnectionType || checkedConnection) ?
          <div className={classnames(styles.createConnections, { [styles.showCreateConnections]: (createConnectionType || checkedConnection) })}>
            <CreateConnection
              createType={createConnectionType}
              editId={checkedConnection}
              closeCreateConnection={() => { setCreateConnectionType(undefined); setCheckedConnection(undefined); }}
              submitCallback={getDataSource}
            />
          </div>
          :
          <div className={styles.dataBaseList}>
            {
              databaseTypeList.map(t => {
                return <div key={t.code} className={styles.databaseItem} onClick={handleCreateConnections.bind(null, t)}>
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
              })
            }
            <div className={styles.databaseItemSpacer}></div>
            <div className={styles.databaseItemSpacer}></div>
          </div>
      }
    </div>
  </div>
});

