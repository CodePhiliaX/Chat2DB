import { DragOutlined, SortAscendingOutlined, SortDescendingOutlined } from '@ant-design/icons';
import React, { useEffect, useMemo, useRef, useState } from 'react';
import { Button, Dropdown, Tooltip } from 'antd';
import classnames from 'classnames';
import i18n from '@/i18n';
// import RefreshLoadingButton from '@/components/RefreshLoadingButton';

// ----- services -----
import connectionService from '@/service/connection';

// ----- constants/typings -----
import { databaseMap } from '@/constants';
import { IConnectionDetails, IConnectionListItem } from '@/typings';

// ----- components -----
import CreateConnection from '@/blocks/CreateConnection';
import Iconfont from '@/components/Iconfont';
import LoadingContent from '@/components/Loading/LoadingContent';
import MenuLabel from '@/components/MenuLabel';

// ----- hooks -----
import useClickAndDoubleClick from '@/hooks/useClickAndDoubleClick';

// ----- store -----
import { getConnectionList, setConnectionList, useConnectionStore } from '@/pages/main/store/connection';
import {
  ConnectionSortMode,
  groupConnectionList,
  sortConnectionList,
} from '@/pages/main/store/connection/utils';
import { setMainPageActiveTab } from '@/pages/main/store/main';
import { setCurrentConnectionDetails } from '@/pages/main/workspace/store/common';
import { getOpenConsoleList } from '@/pages/main/workspace/store/console';

import styles from './index.less';

const ConnectionsPage = () => {
  const { connectionList } = useConnectionStore((state) => {
    return {
      connectionList: state.connectionList,
    };
  });
  const volatileRef = useRef<any>();
  const [connectionActiveId, setConnectionActiveId] = useState<IConnectionListItem['id'] | null>(null);
  const [connectionDetail, setConnectionDetail] = useState<IConnectionDetails | null | undefined>(null);
  const [sortMode, setSortMode] = useState<ConnectionSortMode>('manual');
  const [dragConnectionId, setDragConnectionId] = useState<IConnectionListItem['id'] | null>(null);

  const connectionGroups = useMemo(() => {
    return groupConnectionList(connectionList || [], sortMode);
  }, [connectionList, sortMode]);

  // 处理列表单击事件
  const handleMenuItemSingleClick = (t: IConnectionListItem) => {
    if (connectionActiveId !== t.id) {
      setConnectionActiveId(t.id);
    }
  };

  // 处理列表双击事件
  const handleMenuItemDoubleClick = (t: IConnectionListItem) => {
    setCurrentConnectionDetails(t);
    setMainPageActiveTab('workspace');
  };

  // 处理列表单击和双击事件
  const handleClickConnectionMenu = useClickAndDoubleClick(handleMenuItemSingleClick, handleMenuItemDoubleClick);

  // 切换连接的详情
  useEffect(() => {
    if (!connectionActiveId) {
      return;
    }
    setConnectionDetail(undefined);
    connectionService
      .getDetails({ id: connectionActiveId })
      .then((res) => {
        setConnectionDetail(res);
      })
      .catch(() => {
        setConnectionActiveId(null);
      });
  }, [connectionActiveId]);

  //
  const createDropdownItems = (t) => {
    const handelDelete = (e) => {
      // 禁止冒泡到menuItem
      e.domEvent?.stopPropagation?.();
      connectionService.remove({ id: t.id }).then(() => {
        getConnectionList().then(() => {
          // 连接删除后需要更新下 consoleList
          getOpenConsoleList();
        });
        if (connectionActiveId === t.id) {
          setConnectionActiveId(null);
          setConnectionDetail(null);
        }
      });
    };

    const enterWorkSpace = (e) => {
      e.domEvent?.stopPropagation?.();
      handleMenuItemDoubleClick(t);
    };

    const copyConnection = (e) => {
      e.domEvent?.stopPropagation?.();
      connectionService.clone({ id: t.id }).then((res) => {
        getConnectionList();
        setConnectionActiveId(res);
      });
    };

    return [
      {
        key: 'enterWorkSpace',
        label: <MenuLabel icon="&#xec57;" label={i18n('connection.button.connect')} />,
        onClick: enterWorkSpace,
      },
      {
        key: 'copyConnection',
        label: <MenuLabel icon="&#xec7a;" label={i18n('common.button.copy')} />,
        onClick: copyConnection,
      },
      {
        key: 'delete',
        label: <MenuLabel icon="&#xe6a7;" label={i18n('connection.button.remove')} />,
        onClick: handelDelete,
      },
    ];
  };

  const handleDragConnection = (targetConnection: IConnectionListItem) => {
    if (!connectionList || !dragConnectionId || dragConnectionId === targetConnection.id) {
      return;
    }

    // Manual mode uses the backend-returned order as the source of truth.
    const sortedConnectionList = sortConnectionList(connectionList, 'manual');
    const currentIndex = sortedConnectionList.findIndex((item) => item.id === dragConnectionId);
    const targetIndex = sortedConnectionList.findIndex((item) => item.id === targetConnection.id);

    if (currentIndex === -1 || targetIndex === -1) {
      return;
    }

    const nextConnectionList = [...sortedConnectionList];
    const [dragItem] = nextConnectionList.splice(currentIndex, 1);
    nextConnectionList.splice(targetIndex, 0, dragItem);
    setConnectionList(nextConnectionList);
    setSortMode('manual');
    // Optimistically update the list, then roll back from the server if saving fails.
    connectionService.updateSort({ idList: nextConnectionList.map((item) => item.id) }).catch(() => {
      getConnectionList();
    });
  };

  const switchSortMode = () => {
    // Cycle through manual order and temporary name sorts without overwriting the saved order.
    const nextSortMode = sortMode === 'manual' ? 'asc' : sortMode === 'asc' ? 'desc' : 'manual';
    setSortMode(nextSortMode);
  };

  const sortTooltip = useMemo(() => {
    if (sortMode === 'manual') {
      return i18n('connection.sort.asc');
    }

    if (sortMode === 'asc') {
      return i18n('connection.sort.desc');
    }

    return i18n('connection.sort.manual');
  }, [sortMode]);

  const renderConnectionMenuList = () => {
    return connectionGroups.map((group) => {
      return (
        <div key={group.key} className={styles.connectionGroup}>
          <div className={styles.groupTitle}>
            <span
              className={styles.envTag}
              style={{ background: group.environment?.color?.toLocaleLowerCase() }}
            />
            <span className={styles.groupName}>{group.environment?.name || i18n('connection.group.unknown')}</span>
            <span className={styles.groupCount}>{group.connections.length}</span>
          </div>
          {group.connections.map((t) => {
            return (
              <Dropdown
                key={t.id}
                trigger={['contextMenu']}
                menu={{
                  items: createDropdownItems(t),
                }}
              >
                <div
                  draggable={sortMode === 'manual'}
                  className={classnames(styles.menuItem, {
                    [styles.menuItemActive]: connectionActiveId === t.id,
                    [styles.menuItemDragging]: dragConnectionId === t.id,
                  })}
                  onDragStart={(event) => {
                    event.dataTransfer.effectAllowed = 'move';
                    setDragConnectionId(t.id);
                  }}
                  onDragOver={(event) => {
                    if (sortMode === 'manual') {
                      event.preventDefault();
                    }
                  }}
                  onDrop={(event) => {
                    event.preventDefault();
                    handleDragConnection(t);
                  }}
                  onDragEnd={() => {
                    setDragConnectionId(null);
                  }}
                  onClick={() => {
                    handleClickConnectionMenu(t);
                  }}
                >
                  <div className={classnames(styles.menuItemsTitle)}>
                    {sortMode === 'manual' && <DragOutlined className={styles.dragIcon} />}
                    <span className={styles.databaseTypeIcon}>
                      {<Iconfont className={styles.menuItemIcon} code={databaseMap[t.type]?.icon} />}
                    </span>
                    <span className={styles.name}>{t.alias}</span>
                  </div>
                </div>
              </Dropdown>
            );
          })}
        </div>
      );
    });
  };

  const onSubmit = (data) => {
    return connectionService
      .save({
        ...data,
      })
      .then((res) => {
        getConnectionList();
        setConnectionActiveId(res);
      });
  };

  return (
    <>
      <div className={styles.box}>
        <div ref={volatileRef} className={styles.layoutLeft}>
          <div className={styles.pageHeader}>
            <div className={styles.pageTitle}>{i18n('connection.title.connections')}</div>
            <Tooltip title={sortTooltip}>
              <Button
                type="text"
                size="small"
                className={styles.sortButton}
                onClick={switchSortMode}
              >
                {sortMode === 'manual' ? (
                  <DragOutlined />
                ) : sortMode === 'asc' ? (
                  <SortAscendingOutlined />
                ) : (
                  <SortDescendingOutlined />
                )}
              </Button>
            </Tooltip>
          </div>
          <div className={styles.menuBox}>{renderConnectionMenuList()}</div>
          {connectionActiveId && (
            <Button
              type="primary"
              className={styles.addConnection}
              onClick={() => {
                setConnectionActiveId(null);
                setConnectionDetail(null);
              }}
            >
              {i18n('connection.button.addConnection')}
            </Button>
          )}
        </div>
        <LoadingContent
          className={styles.layoutRight}
          isLoading={connectionDetail === undefined && !!connectionActiveId}
        >
          <CreateConnection connectionDetail={connectionDetail} onSubmit={onSubmit} />
        </LoadingContent>
      </div>
    </>
  );
};

export default ConnectionsPage;
