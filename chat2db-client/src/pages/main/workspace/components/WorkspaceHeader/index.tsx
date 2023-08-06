import React, { memo, useEffect, useMemo, useState } from 'react';
import classnames from 'classnames';
import { connect } from 'umi';
import lodash from 'lodash';
import Iconfont from '@/components/Iconfont';
import { IConnectionModelType } from '@/models/connection';
import { IWorkspaceModelType } from '@/models/workspace';
import { IMainPageType } from '@/models/mainPage';
import { Cascader, Spin, Modal, Button } from 'antd';
import { databaseMap, TreeNodeType } from '@/constants';
import { treeConfig } from '../Tree/treeConfig';
import { useUpdateEffect } from '@/hooks/useUpdateEffect'
import styles from './index.less';

interface IProps {
  className?: string;
  connectionModel: IConnectionModelType['state'];
  workspaceModel: IWorkspaceModelType['state'];
  mainPageModel: IMainPageType['state'];
  dispatch: any;
}

interface IOption {
  label: string;
  value: number | string;
}

const WorkspaceHeader = memo<IProps>((props) => {
  const { className, connectionModel, workspaceModel, mainPageModel, dispatch } = props;
  const { connectionList, curConnection } = connectionModel;
  const { curWorkspaceParams } = workspaceModel;
  const { curPage } = mainPageModel;
  const [cascaderLoading, setCascaderLoading] = useState(false);
  const [noConnectionModal, setNoConnectionModal] = useState(false);
  const [connectionOptions, setConnectionOptions] = useState<IOption[]>([]);
  const [curDBOptions, setCurDBOptions] = useState<IOption[]>([]);
  const [curSchemaOptions, setCurSchemaOptions] = useState<IOption[]>([]);
  const [isRefresh, setIsRefresh] = useState(false);

  useEffect(() => {
    if (curPage !== 'workspace') {
      return
    }
    // 如果没有curConnection默认选第一个
    if (!curConnection?.id && connectionList.length) {
      connectionChange([connectionList[0].id], [connectionList[0]]);
      return
    }
    // 如果都有的话
    if (curConnection?.id && connectionList.length) {
      // 如果curConnection不再connectionList里，也是默认选第一个
      const flag = connectionList.findIndex((t: any) => t.id === curConnection?.id)
      if (flag === -1) {
        connectionChange([connectionList[0].id], [connectionList[0]]);
        return
      }

      // 如果切换了curConnection 导致curWorkspaceParams与curConnection不同
      if (curWorkspaceParams.dataSourceId !== curConnection?.id) {
        setCurWorkspaceParams({
          dataSourceId: curConnection.id,
          dataSourceName: curConnection.alias,
          databaseType: curConnection.type,
        })
        setCurDBOptions([])
        setCurSchemaOptions([])
      }

      // 获取database列表
      getDatabaseList(isRefresh);
      setIsRefresh(false);
    }
  }, [connectionList, curConnection, curPage])

  useUpdateEffect(() => {
    // connectionList转换成可用的ConnectionOptions
    setConnectionOptions(connectionList?.map(t => {
      return {
        value: t.id,
        label: t.alias
      }
    }));
    if (!connectionList.length) {
      dispatch({
        type: 'workspace/setCurWorkspaceParams',
        payload: {}
      })
      dispatch({
        type: 'connection/setCurConnection',
        payload: {}
      })
    }
  }, [connectionList])

  function getDatabaseList(refresh = false) {
    setCascaderLoading(true);
    if (!curConnection?.id) {
      return
    }
    treeConfig[TreeNodeType.DATA_SOURCE].getChildren?.({
      dataSourceId: curConnection.id,
      refresh,
      extraParams: {
        databaseType: curConnection.type,
        dataSourceId: curConnection.id,
        dataSourceName: curConnection.name,
      }
    }).then(res => {
      const dbList = res?.map((t) => {
        return {
          value: t.key,
          label: t.name,
        }
      }) || []
      setCurDBOptions(dbList);
      // 如果是切换那么就默认取列表的第一个database， 如果不是切换那么就取缓存的，如果缓存没有还是取列表第一个（这里是兜底，如果原先他并没有database，后来他加了database，如果还是取缓存的空就不对了）
      const databaseName = curWorkspaceParams.dataSourceId !== curConnection?.id ? dbList[0]?.label : curWorkspaceParams.databaseName || dbList[0]?.label
      getSchemaList(databaseName, refresh);
    }).catch(error => {
      setCascaderLoading(false);
    })
  }

  function getSchemaList(databaseName: string | null | undefined, refresh = false) {
    if (!curConnection?.id) {
      return
    }
    treeConfig[TreeNodeType.DATABASE].getChildren?.({
      dataSourceId: curConnection.id,
      databaseName: databaseName,
      refresh,
      extraParams: {
        databaseName: databaseName,
        databaseType: curConnection.type,
        dataSourceId: curConnection.id,
        dataSourceName: curConnection.name,
      }
    }).then(res => {
      const schemaList = res?.map((t) => {
        return {
          value: t.key,
          label: t.name,
        }
      }) || []
      setCurSchemaOptions(schemaList);
      const schemaName = curWorkspaceParams.dataSourceId !== curConnection?.id ? schemaList[0]?.label : curWorkspaceParams.schemaName || schemaList[0]?.label
      const data: any = {
        dataSourceId: curConnection.id,
        dataSourceName: curConnection.alias,
        databaseType: curConnection.type,
        databaseName: databaseName || null,
        schemaName: schemaName || null
      }

      setCurWorkspaceParams(data)

    }).catch(() => {
      setCurWorkspaceParams({
        dataSourceId: curConnection.id,
        dataSourceName: curConnection.alias,
        databaseType: curConnection.type,
        databaseName: databaseName || null,
      })
    }).finally(() => {
      setCascaderLoading(false)
    })
  }

  function setCurWorkspaceParams(payload: IWorkspaceModelType['state']['curWorkspaceParams']) {
    if (lodash.isEqual(curWorkspaceParams, payload)) {
      return
    }

    dispatch({
      type: 'workspace/setCurWorkspaceParams',
      payload,
    });
  }

  const getConnectionList = () => {
    setCascaderLoading(true);
    setIsRefresh(true);
    dispatch({
      type: 'connection/fetchConnectionList',
      payload: {
        refresh: true
      },
    });
  };

  // 连接切换
  function connectionChange(id: any, data: any) {
    connectionList.map(t => {
      if (t.id === id[0] && curWorkspaceParams.dataSourceId !== id[0]) {
        dispatch({
          type: 'connection/setCurConnection',
          payload: t
        });
      }
    })
  }

  // 数据库切换
  function databaseChange(valueArr: any, selectedOptions: any) {
    if (selectedOptions[0].label !== curWorkspaceParams.databaseName) {
      getSchemaList(selectedOptions[0].label);
    }
  };

  // schema切换
  function schemaChange(valueArr: any, selectedOptions: any) {
    if (selectedOptions[0].label !== curWorkspaceParams.schemaName) {
      setCurWorkspaceParams({ ...curWorkspaceParams, schemaName: selectedOptions[0].value })
    }
  }

  function handelRefresh() {
    getConnectionList();
  }

  return <>
    {
      !!connectionList.length &&
      <div className={styles.workspaceHeader}>
        <Cascader
          popupClassName={styles.cascaderPopup}
          options={connectionOptions}
          onChange={connectionChange}
          bordered={false}
          defaultValue={[curConnection?.id || '']}
        >
          <div className={styles.crumbsItem}>
            <div className={styles.text}>{curWorkspaceParams.dataSourceName}</div>
            <Iconfont className={styles.arrow} code="&#xe641;" />
          </div>
        </Cascader>

        {
          !!curDBOptions?.length &&
          <Cascader
            popupClassName={styles.cascaderPopup}
            options={curDBOptions}
            onChange={databaseChange}
            bordered={false}
            defaultValue={[curWorkspaceParams?.databaseName || '']}
          >
            <div className={styles.crumbsItem}>
              <div className={styles.text}>{curWorkspaceParams.databaseName}</div>
              {
                !!curSchemaOptions.length && <Iconfont className={styles.arrow} code="&#xe608;" />
              }
            </div>
          </Cascader>
        }
        {
          !!curSchemaOptions.length &&
          <Cascader
            popupClassName={styles.cascaderPopup}
            options={curSchemaOptions}
            onChange={schemaChange}
            bordered={false}
            defaultValue={[curWorkspaceParams?.schemaName || '']}
          >
            <div className={styles.crumbsItem}>
              <div className={styles.text}>{curWorkspaceParams.schemaName}</div>
            </div>
          </Cascader>
        }
        <div className={styles.refreshBox} onClick={handelRefresh}>
          {cascaderLoading ? <Spin className={styles.spin} /> : <Iconfont className={styles.typeIcon} code='&#xec08;' />}
        </div>
      </div >
    }
    <Modal
      open={noConnectionModal}
      closeIcon={<></>}
      keyboard={false}
      maskClosable={false}
      title='温馨提示'
      footer={[]}
    >
      <div className={styles.noConnectionModal}>
        <div className={styles.mainText}>
          您当前还没有创建任何连接
        </div>
        <Button type='primary' className={styles.createButton} onClick={() => {
          setNoConnectionModal(false);
          dispatch({
            type: 'mainPage/updateCurPage',
            payload: 'connections',
          });
        }}>创建连接</Button>
      </div>
    </Modal>
  </>
})

export default connect(
  ({ connection, workspace, mainPage }: { connection: IConnectionModelType; workspace: IWorkspaceModelType, mainPage: IMainPageType }) => ({
    connectionModel: connection,
    workspaceModel: workspace,
    mainPageModel: mainPage,
  }),
)(WorkspaceHeader);
