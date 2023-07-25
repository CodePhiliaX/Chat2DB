import React, { memo, useEffect, useMemo, useState } from 'react';
import { connect } from 'umi';
import { IConnectionModelType } from '@/models/connection';
import { IWorkspaceModelType } from '@/models/workspace';
import { IMainPageType } from '@/models/mainPage';
import styles from './index.less';
import classnames from 'classnames';
import { Cascader, Spin, Modal, Button } from 'antd';
import Iconfont from '@/components/Iconfont';
import { databaseMap, TreeNodeType } from '@/constants';
import { treeConfig } from '../Tree/treeConfig';
import lodash from 'lodash';


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
  const [curDBOptions, setCurDBOptions] = useState<IOption[]>([]);
  const [curSchemaOptions, setCurSchemaOptions] = useState<IOption[]>([]);
  const [connectionOptions, setConnectionOptions] = useState<IOption[]>([]);

  // 第一次进入请求连接列表
  useEffect(() => {
    getConnectionList();
  }, []);

  useEffect(() => {
    if (curConnection?.id) {
      if (curWorkspaceParams.dataSourceId !== curConnection?.id) {
        setCurWorkspaceParams({
          dataSourceId: curConnection.id,
          dataSourceName: curConnection.alias,
          databaseType: curConnection.type,
        })
        setCurDBOptions([])
        setCurSchemaOptions([])
      }
      getDatabaseList(false);
    }
  }, [curConnection]);

  function getDatabaseList(refresh = false) {
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
      const databaseName = curWorkspaceParams.dataSourceId !== curConnection?.id ? dbList[0]?.label : curWorkspaceParams.databaseName
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
      const schemaName = curWorkspaceParams.dataSourceId !== curConnection?.id ? schemaList[0]?.label : curWorkspaceParams.schemaName
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

  useEffect(() => {
    if (!curConnection && connectionList.length) {
      connectionChange([connectionList[0].id], [connectionList[0]]);
    }
    const list = connectionList?.map(t => {
      return {
        value: t.id,
        label: t.alias
      }
    })
    setConnectionOptions(list);
  }, [connectionList])

  function setCurWorkspaceParams(payload: IWorkspaceModelType['state']['curWorkspaceParams']) {
    if (lodash.isEqual(curWorkspaceParams, payload)) {
      return
    }

    dispatch({
      type: 'workspace/setCurWorkspaceParams',
      payload,
    });
  }

  const getConnectionList = (refresh = false) => {
    setCascaderLoading(true);
    dispatch({
      type: 'connection/fetchConnectionList',
      payload: {
        refresh
      },
      callback: (res: any) => {
        if (refresh) {
          getDatabaseList(true);
          return
        }
        if (curPage === 'workspace' && !res.data?.length) {
          setNoConnectionModal(true);
          return
        }
        setNoConnectionModal(false);
        if (curConnection?.id && res.data.length) {
          const flag = res.data.findIndex((t: any) => t.id === curConnection?.id)
          if (flag === -1) {
            connectionChange([res.data[0].id], [res.data[0]]);
          }
        }
      }
    });
  };

  // 连接切换
  function connectionChange(id: any, data: any) {
    connectionList.map(t => {
      if (t.id === id[0]) {
        dispatch({
          type: 'connection/setCurConnection',
          payload: t
        });
      }
    })
  }

  // 数据库切换
  function databaseChange(valueArr: any, selectedOptions: any) {
    getSchemaList(selectedOptions[0].label);
  };

  // schema切换
  function schemaChange(valueArr: any, selectedOptions: any) {
    setCurWorkspaceParams({ ...curWorkspaceParams, schemaName: selectedOptions[0].value })
  }

  function handelRefresh() {
    getConnectionList(true);
  }

  return <>
    {<div className={styles.workspaceHeader}>
      <Cascader
        popupClassName={styles.cascaderPopup}
        options={connectionOptions}
        onChange={connectionChange}
        bordered={false}
      // defaultValue={[curConnection?.id]}
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
        // defaultValue={[curWorkspaceParams.databaseName]}
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
        // defaultValue={[curWorkspaceParams.schemaName]}
        >
          <div className={styles.crumbsItem}>
            <div className={styles.text}>{curWorkspaceParams.schemaName}</div>
          </div>
        </Cascader>
      }
      <div className={styles.refreshBox} onClick={handelRefresh}>
        {cascaderLoading ? <Spin className={styles.spin} /> : <Iconfont className={styles.typeIcon} code='&#xec08;' />}
      </div>
    </div >}
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
