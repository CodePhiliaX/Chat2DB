import React, { memo, useEffect, useMemo, useState } from 'react';
import { connect } from 'umi';
import { IConnectionModelType } from '@/models/connection';
import { IWorkspaceModelType } from '@/models/workspace';
import { IMainPageType } from '@/models/mainPage';
import styles from './index.less';
import classnames from 'classnames';
import { Cascader, Spin, Modal, Button } from 'antd';
import Iconfont from '@/components/Iconfont';
import { databaseMap } from '@/constants';
import { useSafeState } from 'ahooks';


interface IProps {
  className?: string;
  cascaderOptions: any;
  connectionModel: IConnectionModelType['state'];
  workspaceModel: IWorkspaceModelType['state'];
  mainPageModel: IMainPageType['state'];
  dispatch: any;
}

const WorkspaceHeader = memo<IProps>((props) => {
  const { className, cascaderOptions, connectionModel, workspaceModel, mainPageModel, dispatch } = props;
  const { connectionList, curConnection } = connectionModel;
  const { curWorkspaceParams } = workspaceModel;
  const { curPage } = mainPageModel;
  const [curSchemaOptions, setCurSchemaOptions] = useState<any>([]);
  const [cascaderLoading, setCascaderLoading] = useState(false);
  const [noConnectionModal, setNoConnectionModal] = useState(false);

  const databaseChange: any = (valueArr: any, selectedOptions: any) => {

    const curWorkspaceParams = {
      dataSourceId: curConnection?.id,
      databaseSourceName: curConnection?.alias,
      databaseName: selectedOptions[0].value,
      schemaName: selectedOptions?.[0]?.next?.[0]?.value,
      databaseType: curConnection?.type,
    };

    dispatch({
      type: 'workspace/setCurWorkspaceParams',
      payload: curWorkspaceParams,
    });

    setCurSchemaOptions(selectedOptions[0].next || [])
  };

  const schemaChange: any = (valueArr: any, selectedOptions: any) => {
    dispatch({
      type: 'workspace/setCurWorkspaceParams',
      payload: { ...curWorkspaceParams, schemaName: selectedOptions[0].value },
    });
  }

  const connectionListOptions = useMemo(() => {
    if (!curConnection && connectionList.length) {
      connectionChange([connectionList[0].id], [connectionList[0]]);
    }
    return connectionList?.map(t => {
      return {
        value: t.id,
        label: t.alias
      }
    })
  }, [connectionList])

  useEffect(() => {
    if (curConnection?.id) {
      getConnectionList();
    }
  }, []);



  useEffect(() => {
    if (curPage === 'workspace' && !connectionList.length) {
      setNoConnectionModal(true)
      return
    }
    setNoConnectionModal(false)
  }, [curPage])

  const getConnectionList = () => {
    setCascaderLoading(true)
    dispatch({
      type: 'connection/fetchConnectionList',
      callback: () => {
        setTimeout(() => {
          setCascaderLoading(false)
        }, 200);
      }
    });
  };

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

  function handelRefresh() {
    getConnectionList();
  }

  return <>

    {curConnection && !!connectionList.length && curWorkspaceParams && <div className={styles.workspaceHeader}>
      <div className={styles.databaseLogo}>
        {curConnection?.type ?
          <div className={styles.refreshBox} onClick={handelRefresh}>
            {cascaderLoading ? <Spin className={styles.spin} /> : <Iconfont className={styles.typeIcon} code={databaseMap[curConnection.type]?.icon} />}
          </div>
          :
          <Iconfont className={styles.typeIcon} code="&#xe640;" />}
      </div>
      <Cascader
        popupClassName={styles.cascaderPopup}
        options={connectionListOptions}
        onChange={connectionChange}
        bordered={false}
      >
        <div className={styles.crumbsItem}>
          <div className={styles.text}>{curConnection?.alias}</div>
          <Iconfont className={styles.arrow} code="&#xe641;" />
        </div>
      </Cascader>

      <Cascader
        popupClassName={styles.cascaderPopup}
        options={cascaderOptions}
        onChange={databaseChange}
        bordered={false}
      >
        <div className={styles.crumbsItem}>
          <div className={styles.text}>{curWorkspaceParams.databaseName}</div>
          {
            !!curSchemaOptions.length && <Iconfont className={styles.arrow} code="&#xe608;" />
          }
        </div>
      </Cascader>
      {
        !!curSchemaOptions.length &&
        <Cascader
          popupClassName={styles.cascaderPopup}
          options={curSchemaOptions}
          onChange={schemaChange}
          bordered={false}
        >
          <div className={styles.crumbsItem}>
            <div className={styles.text}>{curWorkspaceParams.schemaName}</div>
          </div>
        </Cascader>
      }
      <Iconfont className={styles.refreshIcon} onClick={handelRefresh} code="&#xec08;" />
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
