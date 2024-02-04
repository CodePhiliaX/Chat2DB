import React, { useRef, useState, Fragment, useEffect } from 'react';
import { Button, Dropdown } from 'antd';
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
import { useConnectionStore, getConnectionList } from '@/store/connection';
import { useGlobalStore } from '@/store/global';
import { useWorkspaceStore } from '@/store/workspace';

import { useStyle } from './style';
import PageTitle from '@/components/PageTitle';
import { DraggablePanel } from '@chat2db/ui';

const ConnectionsPage = () => {
  const { styles, cx } = useStyle();
  const { connectionList } = useConnectionStore((state) => {
    return {
      connectionList: state.connectionList,
    };
  });
  const [connectionActiveId, setConnectionActiveId] = useState<IConnectionListItem['id'] | null>(null);
  const [connectionDetail, setConnectionDetail] = useState<IConnectionDetails | null | undefined>(null);
  const getOpenConsoleList = useWorkspaceStore((state) => state.getOpenConsoleList);
  const setCurrentConnectionDetails = useWorkspaceStore((state) => state.setCurrentConnectionDetails);

  // 处理列表单击事件
  const handleMenuItemSingleClick = (t: IConnectionListItem) => {
    if (connectionActiveId !== t.id) {
      setConnectionActiveId(t.id);
    }
  };

  // 处理列表双击事件
  const handleMenuItemDoubleClick = (t: IConnectionListItem) => {
    setCurrentConnectionDetails(t);
    useGlobalStore.getState().setMainPageActiveTab('workspace');
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

  const renderConnectionMenuList = () => {
    return (
      <div className={styles.connectionListWrapper}>
        {(connectionList || [])?.map((t) => {
          const isActive = connectionActiveId === t.id;
          return (
            <Dropdown
              key={t.id}
              trigger={['contextMenu']}
              menu={{
                items: createDropdownItems(t),
              }}
            >
              <div
                className={cx(styles.connectionItem, isActive && styles.activeConnectionItem)}
                onClick={() => {
                  handleClickConnectionMenu(t);
                }}
              >
                <Iconfont className={styles.connectionItemIcon} code={databaseMap[t.type]?.icon} />
                <span className={styles.connectionItemLabel}>{t.alias}</span>
              </div>
            </Dropdown>
          );
        })}
      </div>
    );
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
    <DraggablePanel direction={'horizontal'} className={styles.container} defaultSize={[15]} minSize={[15, 70]}>
      <div className={styles.containerLeft}>
        <PageTitle title={i18n('connection.title')} />
        {renderConnectionMenuList()}
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
        className={styles.containerRight}
        isLoading={connectionDetail === undefined && !!connectionActiveId}
      >
        <CreateConnection connectionDetail={connectionDetail} onSubmit={onSubmit} className={styles.connectionDetail} />
      </LoadingContent>
    </DraggablePanel>
  );
};

export default ConnectionsPage;
