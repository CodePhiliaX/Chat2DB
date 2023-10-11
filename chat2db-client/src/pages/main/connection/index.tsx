import React, { Fragment, memo, useEffect, useMemo, useRef, useState } from 'react';
import classnames from 'classnames';
import i18n from '@/i18n';
import ConnectionEdit from '@/components/ConnectionEdit';
import Iconfont from '@/components/Iconfont';
import RefreshLoadingButton from '@/components/RefreshLoadingButton';
import connectionService from '@/service/connection';
import { DatabaseTypeCode, databaseMap, databaseTypeList, ConnectionKind } from '@/constants';
import { IDatabase, IConnectionDetails, IConnectionEnv } from '@/typings';
import { Button, Dropdown, Modal, Tag } from 'antd';
import styles from './index.less';
import { connect, history, Dispatch } from 'umi';
import { IConnectionModelType } from '@/models/connection';
import { IWorkspaceModelType } from '@/models/workspace';
import { deepClone } from '@/utils';

interface IMenu {
  key: number;
  label: string;
  icon: React.ReactNode;
  meta: IConnectionDetails;
  env: IConnectionEnv;
}

interface IAllMenuList {
  [ConnectionKind.Private]: {
    list: IMenu[];
    name: string;
    loading: boolean;
  },
  [ConnectionKind.Shared]: {
    list: IMenu[];
    name: string;
    loading: boolean;
  },
}

interface IProps {
  connectionModel: IConnectionModelType['state'];
  dispatch: any;
}

function Connections(props: IProps) {
  const { connectionModel, dispatch } = props;
  const { connectionList, connectionEnvList } = connectionModel;
  const volatileRef = useRef<any>();
  const [curConnection, setCurConnection] = useState<Partial<IConnectionDetails>>({});
  const [allMenuList, setAllMenuList] = useState<IAllMenuList>();

  useEffect(() => {
    const list: IAllMenuList = {
      [ConnectionKind.Private]: {
        list: [],
        name: i18n('connection.label.private'),
        loading: false,
      },
      [ConnectionKind.Shared]: {
        list: [],
        name: i18n('connection.label.shared'),
        loading: false,
      }
    }
    connectionList.forEach(t => {
      const menu = {
        key: t.id,
        icon: <Iconfont className={styles.menuItemIcon} code={databaseMap[t.type]?.icon} />,
        label: t.alias,
        meta: t,
        env: t.environment,
      }
      if (t.kind === ConnectionKind.Shared) {
        list[ConnectionKind.Shared].list.push(menu)
      } else {
        list[ConnectionKind.Private].list.push(menu)
      }
    });
    setAllMenuList(list);
  }, [connectionList])

  function handleCreateConnections(database: IDatabase) {
    setCurConnection({
      type: database.code,
    });
  }

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

  const handleEnvRefresh = (kind: ConnectionKind) => {
    let p = {
      pageNo: 1,
      pageSize: 999,
      refresh: true,
      kind,
    }
    if (allMenuList) {
      setAllMenuList({
        ...allMenuList,
        [kind]: {
          ...allMenuList[kind],
          loading: true,
        }
      })
    }
    connectionService.getList(p).then(res => {
      if (allMenuList) {
        setAllMenuList({
          ...allMenuList,
          [kind]: {
            ...allMenuList[kind],
            list: res.data.map(t => {
              return {
                key: t.id,
                icon: <Iconfont className={styles.menuItemIcon} code={databaseMap[t.type]?.icon} />,
                label: t.alias,
                meta: t,
                env: t.environment,
              }
            }),
            loading: false,
          }
        })
      }
    });
  }

  const renderMenu = () => {
    return (
      <div className={styles.menuBox}>
        {allMenuList && Object.keys(allMenuList).map(t => {
          const data = allMenuList[t as ConnectionKind];
          if (data.list?.length) {
            return (
              <>
                <div className={styles.envLabel}>
                  <div>{data.name}</div>
                  <RefreshLoadingButton loading={data.loading} className={styles.envRefreshBox} onClick={() => handleEnvRefresh(t as ConnectionKind)} />
                </div>
                {(data.list || []).map(t => {
                  const { key, label, icon } = t;
                  return (
                    <div
                      key={key}
                      className={classnames(styles.menuItem, {
                        [styles.menuItemActive]: curConnection.id === key,
                      })}
                      onDoubleClick={handleMenuItemDoubleClick.bind(null, t)}
                      onClick={(event) => {
                        if (curConnection.id !== t.meta?.id) {
                          setCurConnection(t.meta);
                        }
                      }}
                    >
                      <div className={classnames(styles.menuItemsTitle)}>
                        <span className={styles.envTag} style={{background:t.env.color.toLocaleLowerCase()}}></span>
                        <span className={styles.databaseTypeIcon}>
                          {icon}
                        </span>
                        {/* <Tag className={styles.envTag} color={t.env.color.toLocaleLowerCase()}>
                          {t.env.shortName?.[0]}
                        </Tag> */}
                        <span className={styles.name}>{label}</span>
                      </div>
                      <Dropdown
                        menu={{
                          items: [
                            {
                              key: 'EnterWorkSpace',
                              label: i18n('connection.button.connect'),
                              onClick: ({ domEvent }) => {
                                domEvent.stopPropagation();
                                handleMenuItemDoubleClick(t);
                              },
                            },
                            {
                              key: 'Delete',
                              label: i18n('common.button.delete'),
                              onClick: ({ domEvent }) => {
                                // 禁止冒泡到menuItem
                                domEvent.stopPropagation();
                                connectionService.remove({ id: key }).then(() => {
                                  if (curConnection.id === key) {
                                    setCurConnection({});
                                  }
                                  // // 如果当前工作区正好选中了这个连接，那么就把当前工作区的记录清空
                                  // if (curWorkspaceParams.dataSourceId === key) {
                                  //   dispatch({
                                  //     type: 'workspace/setCurWorkspaceParams',
                                  //     payload: {}
                                  //   })
                                  //   dispatch({
                                  //     type: 'connection/setCurConnection',
                                  //     payload: {}
                                  //   })
                                  // }
                                  dispatch({
                                    type: 'connection/fetchConnectionList',
                                  });
                                });
                              },
                            },
                          ],
                        }}
                      >
                        <div
                          className={styles.moreButton}
                          onClick={(e) => {
                            e.stopPropagation();
                          }}
                        >
                          <Iconfont code="&#xe601;"></Iconfont>
                        </div>
                      </Dropdown>
                    </div>
                  );
                })}
              </>
            );
          }
        })}
      </div>
    );
  };

  return (
    <div className={styles.box}>
      <div ref={volatileRef} className={styles.layoutLeft}>
        <div className={styles.pageTitle}>{i18n('connection.title.connections')}</div>
        {renderMenu()}
        {curConnection && !!Object.keys(curConnection).length && (
          <Button
            type="primary"
            className={styles.addConnection}
            onClick={() => {
              setCurConnection({});
            }}
          >
            {i18n('connection.button.addConnection')}
          </Button>
        )}
      </div>
      <div className={styles.layoutRight}>
        {curConnection && Object.keys(curConnection).length ? (
          <div
            className={classnames(styles.createConnections, {
              [styles.showCreateConnections]: Object.keys(curConnection).length,
            })}
          >
            {
              <ConnectionEdit
                connectionData={curConnection as any}
                closeCreateConnection={() => {
                  setCurConnection({});
                }}
                submitCallback={() => {
                  dispatch({
                    type: 'connection/fetchConnectionList',
                    callback: (res: any) => {
                      setCurConnection(res.data[res.data?.length - 1]);
                    },
                  });
                }}
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
