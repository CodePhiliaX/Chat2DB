import React, { memo, useEffect, useMemo, useState } from 'react';
import { connect } from 'umi';
import { IConnectionModelType } from '@/models/connection';
import { IWorkspaceModelType } from '@/models/workspace';
import styles from './index.less';
import classnames from 'classnames';
import { Cascader, Spin } from 'antd';
import Iconfont from '@/components/Iconfont';
import { databaseMap } from '@/constants';
import { useSafeState } from 'ahooks';


interface IProps {
  className?: string;
  cascaderOptions: any;
  connectionModel: IConnectionModelType['state'];
  workspaceModel: IWorkspaceModelType['state'];
  dispatch: any;
}

const WorkspaceHeader = memo<IProps>((props) => {
  const { className, cascaderOptions, connectionModel, workspaceModel, dispatch } = props;
  const { connectionList, curConnection } = connectionModel;
  const { curWorkspaceParams } = workspaceModel;
  const [curSchemaOptions, setCurSchemaOptions] = useState<any>([]);
  const [cascaderLoading, setCascaderLoading] = useState(false);
  const connectionListOptions = useMemo(() => {
    return connectionList?.map(t => {
      return {
        value: t.id,
        label: t.alias
      }
    })
  }, [connectionList])

  useEffect(() => {
    getConnectionList();
  }, []);

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

    setCurSchemaOptions(selectedOptions[0].next)
  };

  const schemaChange: any = (valueArr: any, selectedOptions: any) => {
    dispatch({
      type: 'workspace/setCurWorkspaceParams',
      payload: { ...curWorkspaceParams, schemaName: selectedOptions[0].value },
    });
  }

  function handelRefresh() {
    getConnectionList()
  }

  return <div className={styles.workspaceHeader}>
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
        <div>{curConnection?.alias}</div>
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
        <div>{curWorkspaceParams.databaseName}</div>
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
          <div>{curWorkspaceParams.schemaName}</div>
        </div>
      </Cascader>
    }

  </div >
})

export default connect(
  ({ connection, workspace }: { connection: IConnectionModelType; workspace: IWorkspaceModelType }) => ({
    connectionModel: connection,
    workspaceModel: workspace,
  }),
)(WorkspaceHeader);
