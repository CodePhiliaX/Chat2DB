import React, { memo, useRef, useEffect, useMemo, useState } from 'react';
import { connect } from 'umi';
import styles from './index.less';
import DraggableContainer from '@/components/DraggableContainer';
import WorkspaceLeft from './components/WorkspaceLeft';
import WorkspaceRight from './components/WorkspaceRight';
import WorkspaceHeader from './components/WorkspaceHeader';
import { IConnectionModelType } from '@/models/connection';
import { IWorkspaceModelType } from '@/models/workspace';
import LoadingContent from '@/components/Loading/LoadingContent';
import { ConsoleOpenedStatus } from '@/constants';
import Iconfont from '@/components/Iconfont';

interface IProps {
  className?: string;
  workspaceModel: IWorkspaceModelType['state'];
  connectionModel: IConnectionModelType['state'];
  pageLoading: any;
  dispatch: any;
}

const dvaModel = connect(
  ({ connection, workspace, loading }: { connection: IConnectionModelType; workspace: IWorkspaceModelType, loading: any }) => ({
    connectionModel: connection,
    workspaceModel: workspace,
    pageLoading: loading.effects['workspace/fetchDatabaseAndSchemaLoading'] || loading.effects['workspace/fetchGetSavedConsoleLoading'],
  }),
);

interface Option {
  value: string;
  label: string;
  children?: Option[];
}

const workspace = memo<IProps>((props) => {
  const draggableRef = useRef<any>();
  const { workspaceModel, connectionModel, dispatch, pageLoading } = props;
  const { curConnection } = connectionModel;
  const { curWorkspaceParams } = workspaceModel;
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (pageLoading === true) {
      setLoading(true);
    } else {
      setLoading(false);
    }
  }, [pageLoading])

  useEffect(() => {
    clearData();
  }, [curConnection]);

  useEffect(() => {
    if (curWorkspaceParams.dataSourceId && (curWorkspaceParams?.databaseName || curWorkspaceParams?.schemaName)) {
      getConsoleList();
    }
  }, [curWorkspaceParams]);

  function clearData() {
    dispatch(({
      type: 'workspace/setOpenConsoleList',
      payload: [],
    }))
    dispatch(({
      type: 'workspace/setConsoleList',
      payload: [],
    }))
    dispatch(({
      type: 'workspace/setDatabaseAndSchema',
      payload: undefined,
    }))
    dispatch(({
      type: 'workspace/setCurTableList',
      payload: [],
    }))
  }

  function getConsoleList() {
    let p: any = {
      pageNo: 1,
      pageSize: 999,
      tabOpened: ConsoleOpenedStatus.IS_OPEN,
      ...curWorkspaceParams,
    };

    dispatch({
      type: 'workspace/fetchGetSavedConsoleLoading',
      payload: p,
      callback: (res: any) => {
        dispatch({
          type: 'workspace/setOpenConsoleList',
          payload: res.data,
        });
      },
    });
  }

  return (
    <div className={styles.workspace}>
      <WorkspaceHeader></WorkspaceHeader>
      <LoadingContent coverLoading={true} isLoading={loading}>
        <DraggableContainer className={styles.workspaceMain}>
          <div ref={draggableRef} className={styles.boxLeft}>
            <WorkspaceLeft />
          </div>
          <div className={styles.boxRight}>
            <WorkspaceRight />
          </div>
        </DraggableContainer>
      </LoadingContent >
    </div>
  );
});

export default dvaModel(workspace)